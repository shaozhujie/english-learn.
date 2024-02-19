package com.ape.apeadmin.controller.syntax;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeSyntaxUser;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.domain.ApeVocabularyUser;
import com.ape.apesystem.service.ApeSyntaxUserService;
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
 * @description: 用户语法controller
 * @date 2024/02/01 11:20
 */
@Controller
@ResponseBody
@RequestMapping("user")
public class ApeSyntaxUserController {

    @Autowired
    private ApeSyntaxUserService apeSyntaxUserService;

    /** 分页获取用户语法 */
    @Log(name = "分页获取用户语法", type = BusinessType.OTHER)
    @PostMapping("getApeSyntaxUserPage")
    public Result getApeSyntaxUserPage(@RequestBody ApeSyntaxUser apeSyntaxUser) {
        Page<ApeSyntaxUser> page = new Page<>(apeSyntaxUser.getPageNumber(),apeSyntaxUser.getPageSize());
        QueryWrapper<ApeSyntaxUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeSyntaxUser.getSyntaxId()),ApeSyntaxUser::getSyntaxId,apeSyntaxUser.getSyntaxId())
                .eq(StringUtils.isNotBlank(apeSyntaxUser.getContent()),ApeSyntaxUser::getContent,apeSyntaxUser.getContent())
                .eq(StringUtils.isNotBlank(apeSyntaxUser.getChinese()),ApeSyntaxUser::getChinese,apeSyntaxUser.getChinese())
                .eq(StringUtils.isNotBlank(apeSyntaxUser.getUserId()),ApeSyntaxUser::getUserId,apeSyntaxUser.getUserId())
                .eq(StringUtils.isNotBlank(apeSyntaxUser.getCreateBy()),ApeSyntaxUser::getCreateBy,apeSyntaxUser.getCreateBy())
                .eq(apeSyntaxUser.getCreateTime() != null,ApeSyntaxUser::getCreateTime,apeSyntaxUser.getCreateTime())
                .eq(StringUtils.isNotBlank(apeSyntaxUser.getUpdateBy()),ApeSyntaxUser::getUpdateBy,apeSyntaxUser.getUpdateBy())
                .eq(apeSyntaxUser.getUpdateTime() != null,ApeSyntaxUser::getUpdateTime,apeSyntaxUser.getUpdateTime());
        Page<ApeSyntaxUser> apeSyntaxUserPage = apeSyntaxUserService.page(page, queryWrapper);
        return Result.success(apeSyntaxUserPage);
    }

    @GetMapping("getSyntaxCountByUserAndV")
    public Result getCountByUserAndV(@RequestParam("id")String id) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ApeSyntaxUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeSyntaxUser::getUserId,userInfo.getId())
                .eq(ApeSyntaxUser::getSyntaxId,id).last("limit 1");
        ApeSyntaxUser one = apeSyntaxUserService.getOne(queryWrapper);
        if (one != null) {
            return Result.success(one);
        } else {
            return Result.fail();
        }
    }

    /** 根据id获取用户语法 */
    @Log(name = "根据id获取用户语法", type = BusinessType.OTHER)
    @GetMapping("getApeSyntaxUserById")
    public Result getApeSyntaxUserById(@RequestParam("id")String id) {
        ApeSyntaxUser apeSyntaxUser = apeSyntaxUserService.getById(id);
        return Result.success(apeSyntaxUser);
    }

    /** 保存用户语法 */
    @Log(name = "保存用户语法", type = BusinessType.INSERT)
    @PostMapping("saveApeSyntaxUser")
    public Result saveApeSyntaxUser(@RequestBody ApeSyntaxUser apeSyntaxUser) {
        apeSyntaxUser.setUserId(ShiroUtils.getUserInfo().getId());
        boolean save = apeSyntaxUserService.save(apeSyntaxUser);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑用户语法 */
    @Log(name = "编辑用户语法", type = BusinessType.UPDATE)
    @PostMapping("editApeSyntaxUser")
    public Result editApeSyntaxUser(@RequestBody ApeSyntaxUser apeSyntaxUser) {
        boolean save = apeSyntaxUserService.updateById(apeSyntaxUser);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除用户语法 */
    @GetMapping("removeApeSyntaxUser")
    @Log(name = "删除用户语法", type = BusinessType.DELETE)
    public Result removeApeSyntaxUser(@RequestParam("id")String id) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ApeSyntaxUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeSyntaxUser::getUserId,userInfo.getId())
                .eq(ApeSyntaxUser::getSyntaxId,id);
        boolean remove = apeSyntaxUserService.remove(queryWrapper);
        if (remove) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

}