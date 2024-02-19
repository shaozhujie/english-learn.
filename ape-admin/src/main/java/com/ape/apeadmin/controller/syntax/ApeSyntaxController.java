package com.ape.apeadmin.controller.syntax;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeSyntax;
import com.ape.apesystem.domain.ApeSyntaxUser;
import com.ape.apesystem.service.ApeSyntaxService;
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
 * @description: 语法controller
 * @date 2024/01/31 10:38
 */
@Controller
@ResponseBody
@RequestMapping("syntax")
public class ApeSyntaxController {

    @Autowired
    private ApeSyntaxService apeSyntaxService;
    @Autowired
    private ApeSyntaxUserService apeSyntaxUserService;

    /** 分页获取语法 */
    @Log(name = "分页获取语法", type = BusinessType.OTHER)
    @PostMapping("getApeSyntaxPage")
    public Result getApeSyntaxPage(@RequestBody ApeSyntax apeSyntax) {
        Page<ApeSyntax> page = new Page<>(apeSyntax.getPageNumber(),apeSyntax.getPageSize());
        QueryWrapper<ApeSyntax> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeSyntax.getType()),ApeSyntax::getType,apeSyntax.getType())
                .like(StringUtils.isNotBlank(apeSyntax.getContent()),ApeSyntax::getContent,apeSyntax.getContent())
                .like(StringUtils.isNotBlank(apeSyntax.getChinese()),ApeSyntax::getChinese,apeSyntax.getChinese())
                .orderByDesc(ApeSyntax::getCreateTime);

        Page<ApeSyntax> apeSyntaxPage = apeSyntaxService.page(page, queryWrapper);
        return Result.success(apeSyntaxPage);
    }

    /** 根据id获取语法 */
    @Log(name = "根据id获取语法", type = BusinessType.OTHER)
    @GetMapping("getApeSyntaxById")
    public Result getApeSyntaxById(@RequestParam("id")String id) {
        ApeSyntax apeSyntax = apeSyntaxService.getById(id);
        return Result.success(apeSyntax);
    }

    /** 保存语法 */
    @Log(name = "保存语法", type = BusinessType.INSERT)
    @PostMapping("saveApeSyntax")
    public Result saveApeSyntax(@RequestBody ApeSyntax apeSyntax) {
        boolean save = apeSyntaxService.save(apeSyntax);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑语法 */
    @Log(name = "编辑语法", type = BusinessType.UPDATE)
    @PostMapping("editApeSyntax")
    public Result editApeSyntax(@RequestBody ApeSyntax apeSyntax) {
        boolean save = apeSyntaxService.updateById(apeSyntax);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除语法 */
    @GetMapping("removeApeSyntax")
    @Log(name = "删除语法", type = BusinessType.DELETE)
    @Transactional(rollbackFor = Exception.class)
    public Result removeApeSyntax(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeSyntaxService.removeById(id);
                QueryWrapper<ApeSyntaxUser> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ApeSyntaxUser::getSyntaxId,id);
                apeSyntaxUserService.remove(queryWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("语法id不能为空！");
        }
    }

}