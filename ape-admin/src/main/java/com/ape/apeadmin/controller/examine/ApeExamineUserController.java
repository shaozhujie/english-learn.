package com.ape.apeadmin.controller.examine;

import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeExamineItemUser;
import com.ape.apesystem.domain.ApeExamineUser;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeExamineItemUserService;
import com.ape.apesystem.service.ApeExamineUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 用户自测controller
 * @date 2024/02/02 04:32
 */
@Controller
@ResponseBody
@RequestMapping("user")
public class ApeExamineUserController {

    @Autowired
    private ApeExamineUserService apeExamineUserService;
    @Autowired
    private ApeExamineItemUserService apeExamineItemUserService;

    /** 分页获取用户自测 */
    @Log(name = "分页获取用户自测", type = BusinessType.OTHER)
    @PostMapping("getApeExamineUserPage")
    public Result getApeExamineUserPage(@RequestBody ApeExamineUser apeExamineUser) {
        Page<ApeExamineUser> page = new Page<>(apeExamineUser.getPageNumber(),apeExamineUser.getPageSize());
        QueryWrapper<ApeExamineUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeExamineUser.getExamineId()),ApeExamineUser::getExamineId,apeExamineUser.getExamineId())
                .eq(StringUtils.isNotBlank(apeExamineUser.getName()),ApeExamineUser::getName,apeExamineUser.getName())
                .eq(StringUtils.isNotBlank(apeExamineUser.getIntroduction()),ApeExamineUser::getIntroduction,apeExamineUser.getIntroduction())
                .eq(StringUtils.isNotBlank(apeExamineUser.getContent()),ApeExamineUser::getContent,apeExamineUser.getContent())
                .eq(StringUtils.isNotBlank(apeExamineUser.getAudio()),ApeExamineUser::getAudio,apeExamineUser.getAudio())
                .eq(apeExamineUser.getType() != null,ApeExamineUser::getType,apeExamineUser.getType())
                .eq(StringUtils.isNotBlank(apeExamineUser.getUserId()),ApeExamineUser::getUserId,apeExamineUser.getUserId())
                .eq(StringUtils.isNotBlank(apeExamineUser.getCreateBy()),ApeExamineUser::getCreateBy,apeExamineUser.getCreateBy())
                .eq(apeExamineUser.getCreateTime() != null,ApeExamineUser::getCreateTime,apeExamineUser.getCreateTime())
                .eq(StringUtils.isNotBlank(apeExamineUser.getUpdateBy()),ApeExamineUser::getUpdateBy,apeExamineUser.getUpdateBy())
                .eq(apeExamineUser.getUpdateTime() != null,ApeExamineUser::getUpdateTime,apeExamineUser.getUpdateTime())
                .orderByDesc(ApeExamineUser::getCreateTime);
        Page<ApeExamineUser> apeExamineUserPage = apeExamineUserService.page(page, queryWrapper);
        return Result.success(apeExamineUserPage);
    }

    /** 根据id获取用户自测 */
    @Log(name = "根据id获取用户自测", type = BusinessType.OTHER)
    @GetMapping("getApeExamineUserById")
    public Result getApeExamineUserById(@RequestParam("id")String id) {
        ApeExamineUser apeExamineUser = apeExamineUserService.getById(id);
        return Result.success(apeExamineUser);
    }

    /** 保存用户自测 */
    @Log(name = "保存用户自测", type = BusinessType.INSERT)
    @PostMapping("saveApeExamineUser")
    @Transactional(rollbackFor = Exception.class)
    public Result saveApeExamineUser(@RequestBody ApeExamineUser apeExamineUser) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        //先删除用户自测
        QueryWrapper<ApeExamineUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeExamineUser::getExamineId,apeExamineUser.getExamineId())
                .eq(ApeExamineUser::getUserId,userInfo.getId());
        apeExamineUserService.remove(queryWrapper);
        apeExamineUser.setUserId(userInfo.getId());
        //先删除用户题目
        QueryWrapper<ApeExamineItemUser> query = new QueryWrapper<>();
        query.lambda().eq(ApeExamineItemUser::getUserId,userInfo.getId())
                .eq(ApeExamineItemUser::getExamineId,apeExamineUser.getExamineId());
        apeExamineItemUserService.remove(query);
        int right = 0;
        int wrong = 0;
        for (int i = 0; i < apeExamineUser.getItems().size(); i++) {
            ApeExamineItemUser itemUser = apeExamineUser.getItems().get(i);
            itemUser.setExamineItemId(itemUser.getId());
            itemUser.setId(IdWorker.getIdStr());
            itemUser.setUserId(userInfo.getId());
            if (itemUser.getAnswer().equals(itemUser.getResult())) {
                right += 1;
            } else {
                wrong += 1;
            }
            apeExamineItemUserService.save(itemUser);
        }
        boolean save = apeExamineUserService.save(apeExamineUser);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("right",right);
        jsonObject.put("wrong",wrong);
        if (save) {
            return Result.success(jsonObject);
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑用户自测 */
    @Log(name = "编辑用户自测", type = BusinessType.UPDATE)
    @PostMapping("editApeExamineUser")
    public Result editApeExamineUser(@RequestBody ApeExamineUser apeExamineUser) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        apeExamineUser.setUserId(userInfo.getId());
        boolean save = apeExamineUserService.updateById(apeExamineUser);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除用户自测 */
    @GetMapping("removeApeExamineUser")
    @Log(name = "删除用户自测", type = BusinessType.DELETE)
    public Result removeApeExamineUser(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeExamineUserService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("用户自测id不能为空！");
        }
    }

}