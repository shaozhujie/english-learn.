package com.ape.apeadmin.controller.item;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeExamineItem;
import com.ape.apesystem.service.ApeExamineItemService;
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
 * @description: 题目controller
 * @date 2024/02/01 08:15
 */
@Controller
@ResponseBody
@RequestMapping("item")
public class ApeExamineItemController {

    @Autowired
    private ApeExamineItemService apeExamineItemService;

    /** 分页获取题目 */
    @Log(name = "分页获取题目", type = BusinessType.OTHER)
    @PostMapping("getApeExamineItemPage")
    public Result getApeExamineItemPage(@RequestBody ApeExamineItem apeExamineItem) {
        Page<ApeExamineItem> page = new Page<>(apeExamineItem.getPageNumber(),apeExamineItem.getPageSize());
        QueryWrapper<ApeExamineItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeExamineItem.getExamineId()),ApeExamineItem::getExamineId,apeExamineItem.getExamineId())
                .like(StringUtils.isNotBlank(apeExamineItem.getContent()),ApeExamineItem::getContent,apeExamineItem.getContent());
        Page<ApeExamineItem> apeExamineItemPage = apeExamineItemService.page(page, queryWrapper);
        return Result.success(apeExamineItemPage);
    }

    @GetMapping("getApeExamineItemByIdE")
    public Result getApeExamineItemByIdE(@RequestParam("id")String id) {
        QueryWrapper<ApeExamineItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeExamineItem::getExamineId,id).orderByAsc(ApeExamineItem::getSort);
        List<ApeExamineItem> itemList = apeExamineItemService.list(queryWrapper);
        return Result.success(itemList);
    }

    /** 根据id获取题目 */
    @Log(name = "根据id获取题目", type = BusinessType.OTHER)
    @GetMapping("getApeExamineItemById")
    public Result getApeExamineItemById(@RequestParam("id")String id) {
        ApeExamineItem apeExamineItem = apeExamineItemService.getById(id);
        return Result.success(apeExamineItem);
    }

    /** 保存题目 */
    @Log(name = "保存题目", type = BusinessType.INSERT)
    @PostMapping("saveApeExamineItem")
    public Result saveApeExamineItem(@RequestBody ApeExamineItem apeExamineItem) {
        boolean save = apeExamineItemService.save(apeExamineItem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑题目 */
    @Log(name = "编辑题目", type = BusinessType.UPDATE)
    @PostMapping("editApeExamineItem")
    public Result editApeExamineItem(@RequestBody ApeExamineItem apeExamineItem) {
        boolean save = apeExamineItemService.updateById(apeExamineItem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除题目 */
    @GetMapping("removeApeExamineItem")
    @Log(name = "删除题目", type = BusinessType.DELETE)
    public Result removeApeExamineItem(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeExamineItemService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("题目id不能为空！");
        }
    }

}