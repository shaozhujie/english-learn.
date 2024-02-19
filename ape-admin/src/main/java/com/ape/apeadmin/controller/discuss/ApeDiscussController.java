package com.ape.apeadmin.controller.discuss;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.ApeDiscuss;
import com.ape.apesystem.domain.ApeDiscussItem;
import com.ape.apesystem.domain.ApeUser;
import com.ape.apesystem.service.ApeDiscussItemService;
import com.ape.apesystem.service.ApeDiscussService;
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
 * @description: 讨论controller
 * @date 2024/01/31 04:28
 */
@Controller
@ResponseBody
@RequestMapping("discuss")
public class ApeDiscussController {

    @Autowired
    private ApeDiscussService apeDiscussService;
    @Autowired
    private ApeDiscussItemService apeDiscussItemService;

    /** 分页获取讨论 */
    @Log(name = "分页获取讨论", type = BusinessType.OTHER)
    @PostMapping("getApeDiscussPage")
    public Result getApeDiscussPage(@RequestBody ApeDiscuss apeDiscuss) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        Page<ApeDiscuss> page = new Page<>(apeDiscuss.getPageNumber(),apeDiscuss.getPageSize());
        QueryWrapper<ApeDiscuss> queryWrapper = new QueryWrapper<>();
        if (apeDiscuss.getType() == 0) {
            queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeDiscuss.getContent()),ApeDiscuss::getContent,apeDiscuss.getContent())
                .like(StringUtils.isNotBlank(apeDiscuss.getCreateBy()),ApeDiscuss::getCreateBy,apeDiscuss.getCreateBy())
                .orderByDesc(ApeDiscuss::getCreateTime);
        } else if (apeDiscuss.getType() == 1) {
            queryWrapper.lambda().eq(ApeDiscuss::getUserId,userInfo.getId())
                .like(StringUtils.isNotBlank(apeDiscuss.getContent()),ApeDiscuss::getContent,apeDiscuss.getContent())
                .like(StringUtils.isNotBlank(apeDiscuss.getCreateBy()),ApeDiscuss::getCreateBy,apeDiscuss.getCreateBy())
                .orderByDesc(ApeDiscuss::getCreateTime);
        } else {
            Page<ApeDiscussItem> pageItem = new Page<>(apeDiscuss.getPageNumber(),apeDiscuss.getPageSize());
            QueryWrapper<ApeDiscussItem> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(ApeDiscussItem::getUserId,userInfo.getId()).groupBy(ApeDiscussItem::getDiscussId);
            Page<ApeDiscussItem> itemPage = apeDiscussItemService.page(pageItem,wrapper);
            for (ApeDiscussItem apeDiscussItem : itemPage.getRecords()) {
                apeDiscussItem.setId(apeDiscussItem.getDiscussId());
                apeDiscussItem.setContent(apeDiscussItem.getDiscussContent());
            }
            return Result.success(itemPage);
        }
        Page<ApeDiscuss> apeDiscussPage = apeDiscussService.page(page, queryWrapper);
        return Result.success(apeDiscussPage);
    }

    /** 根据id获取讨论 */
    @Log(name = "根据id获取讨论", type = BusinessType.OTHER)
    @GetMapping("getApeDiscussById")
    public Result getApeDiscussById(@RequestParam("id")String id) {
        ApeDiscuss apeDiscuss = apeDiscussService.getById(id);
        return Result.success(apeDiscuss);
    }

    /** 保存讨论 */
    @Log(name = "保存讨论", type = BusinessType.INSERT)
    @PostMapping("saveApeDiscuss")
    public Result saveApeDiscuss(@RequestBody ApeDiscuss apeDiscuss) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        apeDiscuss.setUserId(userInfo.getId());
        boolean save = apeDiscussService.save(apeDiscuss);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑讨论 */
    @Log(name = "编辑讨论", type = BusinessType.UPDATE)
    @PostMapping("editApeDiscuss")
    public Result editApeDiscuss(@RequestBody ApeDiscuss apeDiscuss) {
        boolean save = apeDiscussService.updateById(apeDiscuss);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除讨论 */
    @GetMapping("removeApeDiscuss")
    @Log(name = "删除讨论", type = BusinessType.DELETE)
    public Result removeApeDiscuss(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeDiscussService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("讨论id不能为空！");
        }
    }

}