package com.ape.apeadmin.controller.literature;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeLiterature;
import com.ape.apesystem.domain.ApeLiteratureUser;
import com.ape.apesystem.service.ApeLiteratureService;
import com.ape.apesystem.service.ApeLiteratureUserService;
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
 * @description: 文献controller
 * @date 2024/01/31 11:29
 */
@Controller
@ResponseBody
@RequestMapping("literature")
public class ApeLiteratureController {

    @Autowired
    private ApeLiteratureService apeLiteratureService;
    @Autowired
    private ApeLiteratureUserService apeLiteratureUserService;

    /** 分页获取文献 */
    @Log(name = "分页获取文献", type = BusinessType.OTHER)
    @PostMapping("getApeLiteraturePage")
    public Result getApeLiteraturePage(@RequestBody ApeLiterature apeLiterature) {
        Page<ApeLiterature> page = new Page<>(apeLiterature.getPageNumber(),apeLiterature.getPageSize());
        QueryWrapper<ApeLiterature> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeLiterature.getName()),ApeLiterature::getName,apeLiterature.getName())
                .like(StringUtils.isNotBlank(apeLiterature.getAuthor()),ApeLiterature::getAuthor,apeLiterature.getAuthor());
        Page<ApeLiterature> apeLiteraturePage = apeLiteratureService.page(page, queryWrapper);
        return Result.success(apeLiteraturePage);
    }

    /** 根据id获取文献 */
    @Log(name = "根据id获取文献", type = BusinessType.OTHER)
    @GetMapping("getApeLiteratureById")
    public Result getApeLiteratureById(@RequestParam("id")String id) {
        ApeLiterature apeLiterature = apeLiteratureService.getById(id);
        return Result.success(apeLiterature);
    }

    /** 保存文献 */
    @Log(name = "保存文献", type = BusinessType.INSERT)
    @PostMapping("saveApeLiterature")
    public Result saveApeLiterature(@RequestBody ApeLiterature apeLiterature) {
        boolean save = apeLiteratureService.save(apeLiterature);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑文献 */
    @Log(name = "编辑文献", type = BusinessType.UPDATE)
    @PostMapping("editApeLiterature")
    public Result editApeLiterature(@RequestBody ApeLiterature apeLiterature) {
        boolean save = apeLiteratureService.updateById(apeLiterature);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除文献 */
    @GetMapping("removeApeLiterature")
    @Log(name = "删除文献", type = BusinessType.DELETE)
    @Transactional(rollbackFor = Exception.class)
    public Result removeApeLiterature(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeLiteratureService.removeById(id);
                QueryWrapper<ApeLiteratureUser> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ApeLiteratureUser::getLiteratureId,id);
                apeLiteratureUserService.remove(queryWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("文献id不能为空！");
        }
    }

}