package com.ape.apeadmin.controller.examine;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeExamineItem;
import com.ape.apesystem.domain.ApeExamineItemUser;
import com.ape.apesystem.domain.ApeExamineUser;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeExamineItemUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 用户自测题目controller
 * @date 2024/02/02 04:36
 */
@Controller
@ResponseBody
@RequestMapping("user")
public class ApeExamineItemUserController {

    @Autowired
    private ApeExamineItemUserService apeExamineItemUserService;

    /** 分页获取用户自测题目 */
    @Log(name = "分页获取用户自测题目", type = BusinessType.OTHER)
    @PostMapping("getApeExamineItemUserPage")
    public Result getApeExamineItemUserPage(@RequestBody ApeExamineItemUser apeExamineItemUser) {
        Page<ApeExamineItemUser> page = new Page<>(apeExamineItemUser.getPageNumber(),apeExamineItemUser.getPageSize());
        QueryWrapper<ApeExamineItemUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeExamineItemUser.getExamineItemId()),ApeExamineItemUser::getExamineItemId,apeExamineItemUser.getExamineItemId())
                .eq(StringUtils.isNotBlank(apeExamineItemUser.getExamineId()),ApeExamineItemUser::getExamineId,apeExamineItemUser.getExamineId())
                .eq(StringUtils.isNotBlank(apeExamineItemUser.getContent()),ApeExamineItemUser::getContent,apeExamineItemUser.getContent())
                .eq(StringUtils.isNotBlank(apeExamineItemUser.getAnswer()),ApeExamineItemUser::getAnswer,apeExamineItemUser.getAnswer())
                .eq(StringUtils.isNotBlank(apeExamineItemUser.getSelects()),ApeExamineItemUser::getSelects,apeExamineItemUser.getSelects())
                .eq(apeExamineItemUser.getSort() != null,ApeExamineItemUser::getSort,apeExamineItemUser.getSort())
                .eq(StringUtils.isNotBlank(apeExamineItemUser.getResult()),ApeExamineItemUser::getResult,apeExamineItemUser.getResult())
                .eq(StringUtils.isNotBlank(apeExamineItemUser.getUserId()),ApeExamineItemUser::getUserId,apeExamineItemUser.getUserId())
                .eq(StringUtils.isNotBlank(apeExamineItemUser.getCreateBy()),ApeExamineItemUser::getCreateBy,apeExamineItemUser.getCreateBy())
                .eq(apeExamineItemUser.getCreateTime() != null,ApeExamineItemUser::getCreateTime,apeExamineItemUser.getCreateTime())
                .eq(StringUtils.isNotBlank(apeExamineItemUser.getUpdateBy()),ApeExamineItemUser::getUpdateBy,apeExamineItemUser.getUpdateBy())
                .eq(apeExamineItemUser.getUpdateTime() != null,ApeExamineItemUser::getUpdateTime,apeExamineItemUser.getUpdateTime())
                .orderByDesc(ApeExamineItemUser::getSort);
        Page<ApeExamineItemUser> apeExamineItemUserPage = apeExamineItemUserService.page(page, queryWrapper);
        return Result.success(apeExamineItemUserPage);
    }

    /** 根据id获取用户自测题目 */
    @Log(name = "根据id获取用户自测题目", type = BusinessType.OTHER)
    @GetMapping("getApeExamineItemUserById")
    public Result getApeExamineItemUserById(@RequestParam("id")String id) {
        ApeExamineItemUser apeExamineItemUser = apeExamineItemUserService.getById(id);
        return Result.success(apeExamineItemUser);
    }

    /** 保存用户自测题目 */
    @Log(name = "保存用户自测题目", type = BusinessType.INSERT)
    @PostMapping("saveApeExamineItemUser")
    public Result saveApeExamineItemUser(@RequestBody ApeExamineItemUser apeExamineItemUser) {
        boolean save = apeExamineItemUserService.save(apeExamineItemUser);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑用户自测题目 */
    @Log(name = "编辑用户自测题目", type = BusinessType.UPDATE)
    @PostMapping("editApeExamineItemUser")
    public Result editApeExamineItemUser(@RequestBody ApeExamineItemUser apeExamineItemUser) {
        boolean save = apeExamineItemUserService.updateById(apeExamineItemUser);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    @GetMapping("getApeExamineItemUserByIdE")
    public Result getApeExamineItemUserByIdE(@RequestParam("id")String id) {
        ApeUser user = ShiroUtils.getUserInfo();
        QueryWrapper<ApeExamineItemUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeExamineItemUser::getExamineId,id)
                .eq(ApeExamineItemUser::getUserId,user.getId())
                .orderByAsc(ApeExamineItemUser::getSort);
        List<ApeExamineItemUser> itemList = apeExamineItemUserService.list(queryWrapper);
        return Result.success(itemList);
    }

    /** 删除用户自测题目 */
    @GetMapping("removeApeExamineItemUser")
    @Log(name = "删除用户自测题目", type = BusinessType.DELETE)
    public Result removeApeExamineItemUser(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeExamineItemUserService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("用户自测题目id不能为空！");
        }
    }

}