package com.ape.apeadmin.controller.examine;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeExamine;
import com.ape.apesystem.domain.ApeExamineItem;
import com.ape.apesystem.domain.ApeExamineItemUser;
import com.ape.apesystem.domain.ApeExamineUser;
import com.ape.apesystem.service.ApeExamineItemService;
import com.ape.apesystem.service.ApeExamineItemUserService;
import com.ape.apesystem.service.ApeExamineService;
import com.ape.apesystem.service.ApeExamineUserService;
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
 * @description: 自测controller
 * @date 2024/02/01 07:44
 */
@Controller
@ResponseBody
@RequestMapping("examine")
public class ApeExamineController {

    @Autowired
    private ApeExamineService apeExamineService;
    @Autowired
    private ApeExamineItemService apeExamineItemService;
    @Autowired
    private ApeExamineUserService apeExamineUserService;
    @Autowired
    private ApeExamineItemUserService apeExamineItemUserService;

    /** 分页获取自测 */
    @Log(name = "分页获取自测", type = BusinessType.OTHER)
    @PostMapping("getApeExaminePage")
    public Result getApeExaminePage(@RequestBody ApeExamine apeExamine) {
        Page<ApeExamine> page = new Page<>(apeExamine.getPageNumber(),apeExamine.getPageSize());
        QueryWrapper<ApeExamine> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeExamine.getName()),ApeExamine::getName,apeExamine.getName())
                .like(StringUtils.isNotBlank(apeExamine.getIntroduction()),ApeExamine::getIntroduction,apeExamine.getIntroduction())
                .eq(apeExamine.getType() != null,ApeExamine::getType,apeExamine.getType())
                .orderByDesc(ApeExamine::getCreateTime);
        Page<ApeExamine> apeExaminePage = apeExamineService.page(page, queryWrapper);
        return Result.success(apeExaminePage);
    }

    /** 根据id获取自测 */
    @Log(name = "根据id获取自测", type = BusinessType.OTHER)
    @GetMapping("getApeExamineById")
    public Result getApeExamineById(@RequestParam("id")String id) {
        ApeExamine apeExamine = apeExamineService.getById(id);
        return Result.success(apeExamine);
    }

    /** 保存自测 */
    @Log(name = "保存自测", type = BusinessType.INSERT)
    @PostMapping("saveApeExamine")
    public Result saveApeExamine(@RequestBody ApeExamine apeExamine) {
        boolean save = apeExamineService.save(apeExamine);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑自测 */
    @Log(name = "编辑自测", type = BusinessType.UPDATE)
    @PostMapping("editApeExamine")
    public Result editApeExamine(@RequestBody ApeExamine apeExamine) {
        boolean save = apeExamineService.updateById(apeExamine);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除自测 */
    @GetMapping("removeApeExamine")
    @Log(name = "删除自测", type = BusinessType.DELETE)
    @Transactional(rollbackFor = Exception.class)
    public Result removeApeExamine(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeExamineService.removeById(id);
                QueryWrapper<ApeExamineItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ApeExamineItem::getExamineId,id);
                apeExamineItemService.remove(queryWrapper);

                QueryWrapper<ApeExamineUser> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.lambda().eq(ApeExamineUser::getExamineId,id);
                apeExamineUserService.remove(queryWrapper1);

                QueryWrapper<ApeExamineItemUser> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.lambda().eq(ApeExamineItemUser::getExamineId,id);
                apeExamineItemUserService.remove(queryWrapper2);
            }
            return Result.success();
        } else {
            return Result.fail("自测id不能为空！");
        }
    }

}