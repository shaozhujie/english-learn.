package com.ape.apeadmin.controller.literature;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeLiteratureUser;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.domain.ApeVocabularyUser;
import com.ape.apesystem.service.ApeLiteratureUserService;
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
 * @description: 用户文献controller
 * @date 2024/02/02 02:25
 */
@Controller
@ResponseBody
@RequestMapping("user")
public class ApeLiteratureUserController {

    @Autowired
    private ApeLiteratureUserService apeLiteratureUserService;

    /** 分页获取用户文献 */
    @Log(name = "分页获取用户文献", type = BusinessType.OTHER)
    @PostMapping("getApeLiteratureUserPage")
    public Result getApeLiteratureUserPage(@RequestBody ApeLiteratureUser apeLiteratureUser) {
        Page<ApeLiteratureUser> page = new Page<>(apeLiteratureUser.getPageNumber(),apeLiteratureUser.getPageSize());
        QueryWrapper<ApeLiteratureUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeLiteratureUser.getLiteratureId()),ApeLiteratureUser::getLiteratureId,apeLiteratureUser.getLiteratureId())
                .eq(StringUtils.isNotBlank(apeLiteratureUser.getName()),ApeLiteratureUser::getName,apeLiteratureUser.getName())
                .eq(StringUtils.isNotBlank(apeLiteratureUser.getAuthor()),ApeLiteratureUser::getAuthor,apeLiteratureUser.getAuthor())
                .eq(StringUtils.isNotBlank(apeLiteratureUser.getImage()),ApeLiteratureUser::getImage,apeLiteratureUser.getImage())
                .eq(StringUtils.isNotBlank(apeLiteratureUser.getIntroduction()),ApeLiteratureUser::getIntroduction,apeLiteratureUser.getIntroduction())
                .eq(StringUtils.isNotBlank(apeLiteratureUser.getUserId()),ApeLiteratureUser::getUserId,apeLiteratureUser.getUserId())
                .orderByDesc(ApeLiteratureUser::getCreateTime);
        Page<ApeLiteratureUser> apeLiteratureUserPage = apeLiteratureUserService.page(page, queryWrapper);
        return Result.success(apeLiteratureUserPage);
    }

    @GetMapping("getLCountByUserAndV")
    public Result getLCountByUserAndV(@RequestParam("id")String id) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ApeLiteratureUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeLiteratureUser::getUserId,userInfo.getId())
                .eq(ApeLiteratureUser::getLiteratureId,id).last("limit 1");
        ApeLiteratureUser one = apeLiteratureUserService.getOne(queryWrapper);
        if (one != null) {
            return Result.success(one);
        } else {
            return Result.fail();
        }
    }

    /** 根据id获取用户文献 */
    @Log(name = "根据id获取用户文献", type = BusinessType.OTHER)
    @GetMapping("getApeLiteratureUserById")
    public Result getApeLiteratureUserById(@RequestParam("id")String id) {
        ApeLiteratureUser apeLiteratureUser = apeLiteratureUserService.getById(id);
        return Result.success(apeLiteratureUser);
    }

    /** 保存用户文献 */
    @Log(name = "保存用户文献", type = BusinessType.INSERT)
    @PostMapping("saveApeLiteratureUser")
    public Result saveApeLiteratureUser(@RequestBody ApeLiteratureUser apeLiteratureUser) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        apeLiteratureUser.setUserId(userInfo.getId());
        boolean save = apeLiteratureUserService.save(apeLiteratureUser);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑用户文献 */
    @Log(name = "编辑用户文献", type = BusinessType.UPDATE)
    @PostMapping("editApeLiteratureUser")
    public Result editApeLiteratureUser(@RequestBody ApeLiteratureUser apeLiteratureUser) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        apeLiteratureUser.setUserId(userInfo.getId());
        boolean save = apeLiteratureUserService.updateById(apeLiteratureUser);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除用户文献 */
    @GetMapping("removeApeLiteratureUser")
    @Log(name = "删除用户文献", type = BusinessType.DELETE)
    public Result removeApeLiteratureUser(@RequestParam("id")String id) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        QueryWrapper<ApeLiteratureUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeLiteratureUser::getUserId,userInfo.getId())
                .eq(ApeLiteratureUser::getLiteratureId,id);
        boolean remove = apeLiteratureUserService.remove(queryWrapper);
        if (remove) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

}