package com.ape.apeadmin.controller.literature;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeLiteratureItem;
import com.ape.apesystem.service.ApeLiteratureItemService;
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
 * @description: 文献章节controller
 * @date 2024/01/31 03:04
 */
@Controller
@ResponseBody
@RequestMapping("item")
public class ApeLiteratureItemController {

    @Autowired
    private ApeLiteratureItemService apeLiteratureItemService;

    /** 分页获取文献章节 */
    @Log(name = "分页获取文献章节", type = BusinessType.OTHER)
    @PostMapping("getApeLiteratureItemPage")
    public Result getApeLiteratureItemPage(@RequestBody ApeLiteratureItem apeLiteratureItem) {
        Page<ApeLiteratureItem> page = new Page<>(apeLiteratureItem.getPageNumber(),apeLiteratureItem.getPageSize());
        QueryWrapper<ApeLiteratureItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(apeLiteratureItem.getLiteratureId()),ApeLiteratureItem::getLiteratureId,apeLiteratureItem.getLiteratureId())
                .like(StringUtils.isNotBlank(apeLiteratureItem.getTitle()),ApeLiteratureItem::getTitle,apeLiteratureItem.getTitle())
                .orderByAsc(ApeLiteratureItem::getSort);
        Page<ApeLiteratureItem> apeLiteratureItemPage = apeLiteratureItemService.page(page, queryWrapper);
        return Result.success(apeLiteratureItemPage);
    }

    @GetMapping("getApeLiteratureItemByLid")
    public Result getApeLiteratureItemByLid(@RequestParam("id")String id) {
        QueryWrapper<ApeLiteratureItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeLiteratureItem::getLiteratureId,id).orderByAsc(ApeLiteratureItem::getSort);
        List<ApeLiteratureItem> literatureItemList = apeLiteratureItemService.list(queryWrapper);
        return Result.success(literatureItemList);
    }

    /** 根据id获取文献章节 */
    @Log(name = "根据id获取文献章节", type = BusinessType.OTHER)
    @GetMapping("getApeLiteratureItemById")
    public Result getApeLiteratureItemById(@RequestParam("id")String id) {
        ApeLiteratureItem apeLiteratureItem = apeLiteratureItemService.getById(id);
        return Result.success(apeLiteratureItem);
    }

    /** 保存文献章节 */
    @Log(name = "保存文献章节", type = BusinessType.INSERT)
    @PostMapping("saveApeLiteratureItem")
    public Result saveApeLiteratureItem(@RequestBody ApeLiteratureItem apeLiteratureItem) {
        boolean save = apeLiteratureItemService.save(apeLiteratureItem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑文献章节 */
    @Log(name = "编辑文献章节", type = BusinessType.UPDATE)
    @PostMapping("editApeLiteratureItem")
    public Result editApeLiteratureItem(@RequestBody ApeLiteratureItem apeLiteratureItem) {
        boolean save = apeLiteratureItemService.updateById(apeLiteratureItem);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除文献章节 */
    @GetMapping("removeApeLiteratureItem")
    @Log(name = "删除文献章节", type = BusinessType.DELETE)
    public Result removeApeLiteratureItem(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeLiteratureItemService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("文献章节id不能为空！");
        }
    }

}