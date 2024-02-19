package com.ape.apeadmin.controller.type;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeSyntaxType;
import com.ape.apesystem.service.ApeSyntaxTypeService;
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
 * @description: 语法分类controller
 * @date 2024/01/31 10:46
 */
@Controller
@ResponseBody
@RequestMapping("type")
public class ApeSyntaxTypeController {

    @Autowired
    private ApeSyntaxTypeService apeSyntaxTypeService;

    /** 分页获取语法分类 */
    @Log(name = "分页获取语法分类", type = BusinessType.OTHER)
    @PostMapping("getApeSyntaxTypePage")
    public Result getApeSyntaxTypePage(@RequestBody ApeSyntaxType apeSyntaxType) {
        Page<ApeSyntaxType> page = new Page<>(apeSyntaxType.getPageNumber(),apeSyntaxType.getPageSize());
        QueryWrapper<ApeSyntaxType> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeSyntaxType.getName()),ApeSyntaxType::getName,apeSyntaxType.getName());
        Page<ApeSyntaxType> apeSyntaxTypePage = apeSyntaxTypeService.page(page, queryWrapper);
        return Result.success(apeSyntaxTypePage);
    }

    @GetMapping("/getApeSyntaxTypeList")
    public Result getApeSyntaxTypeList() {
        List<ApeSyntaxType> typeList = apeSyntaxTypeService.list();
        return Result.success(typeList);
    }

    /** 根据id获取语法分类 */
    @Log(name = "根据id获取语法分类", type = BusinessType.OTHER)
    @GetMapping("getApeSyntaxTypeById")
    public Result getApeSyntaxTypeById(@RequestParam("id")String id) {
        ApeSyntaxType apeSyntaxType = apeSyntaxTypeService.getById(id);
        return Result.success(apeSyntaxType);
    }

    /** 保存语法分类 */
    @Log(name = "保存语法分类", type = BusinessType.INSERT)
    @PostMapping("saveApeSyntaxType")
    public Result saveApeSyntaxType(@RequestBody ApeSyntaxType apeSyntaxType) {
        boolean save = apeSyntaxTypeService.save(apeSyntaxType);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑语法分类 */
    @Log(name = "编辑语法分类", type = BusinessType.UPDATE)
    @PostMapping("editApeSyntaxType")
    public Result editApeSyntaxType(@RequestBody ApeSyntaxType apeSyntaxType) {
        boolean save = apeSyntaxTypeService.updateById(apeSyntaxType);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除语法分类 */
    @GetMapping("removeApeSyntaxType")
    @Log(name = "删除语法分类", type = BusinessType.DELETE)
    public Result removeApeSyntaxType(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeSyntaxTypeService.removeById(id);
            }
            return Result.success();
        } else {
            return Result.fail("语法分类id不能为空！");
        }
    }

}