package com.ape.apeadmin.controller.vocabulary;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeVocabulary;
import com.ape.apesystem.domain.ApeVocabularyUser;
import com.ape.apesystem.service.ApeVocabularyService;
import com.ape.apesystem.service.ApeVocabularyUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

/**
 * @author 超级管理员
 * @version 1.0
 * @description: 词汇controller
 * @date 2024/01/31 09:20
 */
@Controller
@ResponseBody
@RequestMapping("vocabulary")
public class ApeVocabularyController {

    @Autowired
    private ApeVocabularyService apeVocabularyService;
    @Autowired
    private ApeVocabularyUserService apeVocabularyUserService;

    /** 分页获取词汇 */
    @Log(name = "分页获取词汇", type = BusinessType.OTHER)
    @PostMapping("getApeVocabularyPage")
    public Result getApeVocabularyPage(@RequestBody ApeVocabulary apeVocabulary) {
        Page<ApeVocabulary> page = new Page<>(apeVocabulary.getPageNumber(),apeVocabulary.getPageSize());
        QueryWrapper<ApeVocabulary> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeVocabulary.getName()),ApeVocabulary::getName,apeVocabulary.getName())
                .like(StringUtils.isNotBlank(apeVocabulary.getDefinition()),ApeVocabulary::getDefinition,apeVocabulary.getDefinition())
                .likeRight(StringUtils.isNotBlank(apeVocabulary.getStart()),ApeVocabulary::getName,apeVocabulary.getStart())
                .orderByAsc(ApeVocabulary::getName).orderByDesc(ApeVocabulary::getCreateTime);
        Page<ApeVocabulary> apeVocabularyPage = apeVocabularyService.page(page, queryWrapper);
        return Result.success(apeVocabularyPage);
    }

    /** 根据id获取词汇 */
    @Log(name = "根据id获取词汇", type = BusinessType.OTHER)
    @GetMapping("getApeVocabularyById")
    public Result getApeVocabularyById(@RequestParam("id")String id) {
        ApeVocabulary apeVocabulary = apeVocabularyService.getById(id);
        return Result.success(apeVocabulary);
    }

    /** 保存词汇 */
    @Log(name = "保存词汇", type = BusinessType.INSERT)
    @PostMapping("saveApeVocabulary")
    public Result saveApeVocabulary(@RequestBody ApeVocabulary apeVocabulary) {
        apeVocabulary.setName(apeVocabulary.getName().substring(0,1).toLowerCase(Locale.ROOT) + apeVocabulary.getName().substring(1));
        boolean save = apeVocabularyService.save(apeVocabulary);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑词汇 */
    @Log(name = "编辑词汇", type = BusinessType.UPDATE)
    @PostMapping("editApeVocabulary")
    public Result editApeVocabulary(@RequestBody ApeVocabulary apeVocabulary) {
        apeVocabulary.setName(apeVocabulary.getName().substring(0,1).toLowerCase(Locale.ROOT) + apeVocabulary.getName().substring(1));
        boolean save = apeVocabularyService.updateById(apeVocabulary);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除词汇 */
    @GetMapping("removeApeVocabulary")
    @Log(name = "删除词汇", type = BusinessType.DELETE)
    @Transactional(rollbackFor = Exception.class)
    public Result removeApeVocabulary(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeVocabularyService.removeById(id);
                QueryWrapper<ApeVocabularyUser> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ApeVocabularyUser::getVocabularyId,id);
                apeVocabularyUserService.remove(queryWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("词汇id不能为空！");
        }
    }

}