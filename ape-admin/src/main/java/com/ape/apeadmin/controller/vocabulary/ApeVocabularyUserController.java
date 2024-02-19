package com.ape.apeadmin.controller.user;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.domain.ApeVocabularyUser;
import com.ape.apesystem.service.ApeVocabularyUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 用户词汇controller
 * @date 2024/02/01 10:22
 */
@Controller
@ResponseBody
@RequestMapping("user")
public class ApeVocabularyUserController {

    @Autowired
    private ApeVocabularyUserService apeVocabularyUserService;

    /** 分页获取用户词汇 */
    @Log(name = "分页获取用户词汇", type = BusinessType.OTHER)
    @PostMapping("getApeVocabularyUserPage")
    public Result getApeVocabularyUserPage(@RequestBody ApeVocabularyUser apeVocabularyUser) {
        Page<ApeVocabularyUser> page = new Page<>(apeVocabularyUser.getPageNumber(),apeVocabularyUser.getPageSize());
        QueryWrapper<ApeVocabularyUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeVocabularyUser.getVocabularyId()),ApeVocabularyUser::getVocabularyId,apeVocabularyUser.getVocabularyId())
                .eq(StringUtils.isNotBlank(apeVocabularyUser.getName()),ApeVocabularyUser::getName,apeVocabularyUser.getName())
                .eq(StringUtils.isNotBlank(apeVocabularyUser.getUserId()),ApeVocabularyUser::getUserId,apeVocabularyUser.getUserId())
                .eq(StringUtils.isNotBlank(apeVocabularyUser.getCreateBy()),ApeVocabularyUser::getCreateBy,apeVocabularyUser.getCreateBy())
                .eq(apeVocabularyUser.getCreateTime() != null,ApeVocabularyUser::getCreateTime,apeVocabularyUser.getCreateTime())
                .eq(StringUtils.isNotBlank(apeVocabularyUser.getUpdateBy()),ApeVocabularyUser::getUpdateBy,apeVocabularyUser.getUpdateBy())
                .eq(apeVocabularyUser.getUpdateTime() != null,ApeVocabularyUser::getUpdateTime,apeVocabularyUser.getUpdateTime());
        Page<ApeVocabularyUser> apeVocabularyUserPage = apeVocabularyUserService.page(page, queryWrapper);
        return Result.success(apeVocabularyUserPage);
    }

    @GetMapping("getCountByUserAndV")
    public Result getCountByUserAndV(@RequestParam("id")String id) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ApeVocabularyUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeVocabularyUser::getUserId,userInfo.getId())
                .eq(ApeVocabularyUser::getVocabularyId,id).last("limit 1");
        ApeVocabularyUser one = apeVocabularyUserService.getOne(queryWrapper);
        if (one != null) {
            return Result.success(one);
        } else {
            return Result.fail();
        }
    }

    /** 根据id获取用户词汇 */
    @Log(name = "根据id获取用户词汇", type = BusinessType.OTHER)
    @GetMapping("getApeVocabularyUserById")
    public Result getApeVocabularyUserById(@RequestParam("id")String id) {
        ApeVocabularyUser apeVocabularyUser = apeVocabularyUserService.getById(id);
        return Result.success(apeVocabularyUser);
    }

    /** 保存用户词汇 */
    @Log(name = "保存用户词汇", type = BusinessType.INSERT)
    @PostMapping("saveApeVocabularyUser")
    public Result saveApeVocabularyUser(@RequestBody ApeVocabularyUser apeVocabularyUser) {
        apeVocabularyUser.setUserId(ShiroUtils.getUserInfo().getId());
        boolean save = apeVocabularyUserService.save(apeVocabularyUser);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑用户词汇 */
    @Log(name = "编辑用户词汇", type = BusinessType.UPDATE)
    @PostMapping("editApeVocabularyUser")
    public Result editApeVocabularyUser(@RequestBody ApeVocabularyUser apeVocabularyUser) {
        boolean save = apeVocabularyUserService.updateById(apeVocabularyUser);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除用户词汇 */
    @GetMapping("removeApeVocabularyUser")
    @Log(name = "删除用户词汇", type = BusinessType.DELETE)
    public Result removeApeVocabularyUser(@RequestParam("id")String id) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ApeVocabularyUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeVocabularyUser::getUserId,userInfo.getId())
                .eq(ApeVocabularyUser::getVocabularyId,id);
        boolean remove = apeVocabularyUserService.remove(queryWrapper);
        if (remove) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }
}