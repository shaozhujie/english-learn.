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
 * @description: 评论controller
 * @date 2024/02/01 07:28
 */
@Controller
@ResponseBody
@RequestMapping("item")
public class ApeDiscussItemController {

    @Autowired
    private ApeDiscussItemService apeDiscussItemService;
    @Autowired
    private ApeDiscussService apeDiscussService;

    /** 分页获取评论 */
    @Log(name = "分页获取评论", type = BusinessType.OTHER)
    @PostMapping("getApeDiscussItemPage")
    public Result getApeDiscussItemPage(@RequestBody ApeDiscussItem apeDiscussItem) {
        Page<ApeDiscussItem> page = new Page<>(apeDiscussItem.getPageNumber(),apeDiscussItem.getPageSize());
        QueryWrapper<ApeDiscussItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeDiscussItem.getDiscussId()),ApeDiscussItem::getDiscussId,apeDiscussItem.getDiscussId())
                .like(StringUtils.isNotBlank(apeDiscussItem.getContent()),ApeDiscussItem::getContent,apeDiscussItem.getContent())
                .eq(StringUtils.isNotBlank(apeDiscussItem.getUserId()),ApeDiscussItem::getUserId,apeDiscussItem.getUserId())
                .eq(StringUtils.isNotBlank(apeDiscussItem.getCreateBy()),ApeDiscussItem::getCreateBy,apeDiscussItem.getCreateBy())
                .orderByDesc(ApeDiscussItem::getCreateTime);
        Page<ApeDiscussItem> apeDiscussItemPage = apeDiscussItemService.page(page, queryWrapper);
        return Result.success(apeDiscussItemPage);
    }

    /** 根据id获取评论 */
    @Log(name = "根据id获取评论", type = BusinessType.OTHER)
    @GetMapping("getApeDiscussItemById")
    public Result getApeDiscussItemById(@RequestParam("id")String id) {
        ApeDiscussItem apeDiscussItem = apeDiscussItemService.getById(id);
        return Result.success(apeDiscussItem);
    }

    /** 保存评论 */
    @Log(name = "保存评论", type = BusinessType.INSERT)
    @PostMapping("saveApeDiscussItem")
    public Result saveApeDiscussItem(@RequestBody ApeDiscussItem apeDiscussItem) {
        ApeDiscuss discuss = apeDiscussService.getById(apeDiscussItem.getDiscussId());
        apeDiscussItem.setDiscussContent(discuss.getContent());
        ApeUser userInfo = ShiroUtils.getUserInfo();
        apeDiscussItem.setUserId(userInfo.getId());
        apeDiscussItem.setAvatar(userInfo.getAvatar());
        boolean save = apeDiscussItemService.save(apeDiscussItem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑评论 */
    @Log(name = "编辑评论", type = BusinessType.UPDATE)
    @PostMapping("editApeDiscussItem")
    public Result editApeDiscussItem(@RequestBody ApeDiscussItem apeDiscussItem) {
        boolean save = apeDiscussItemService.updateById(apeDiscussItem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除评论 */
    @GetMapping("removeApeDiscussItem")
    @Log(name = "删除评论", type = BusinessType.DELETE)
    public Result removeApeDiscussItem(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeDiscussItemService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("评论id不能为空！");
        }
    }

}