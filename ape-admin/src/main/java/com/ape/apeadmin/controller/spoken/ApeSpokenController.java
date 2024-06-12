package com.ape.apeadmin.controller.spoken;

import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apesystem.domain.ApeSpoken;
import com.ape.apesystem.domain.ApeSpokenUser;
import com.ape.apesystem.service.ApeSpokenService;
import com.ape.apesystem.service.ApeSpokenUserService;
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
 * @description: 口语controller
 * @date 2024/01/31 03:54
 */
@Controller
@ResponseBody
@RequestMapping("spoken")
public class ApeSpokenController {

    @Autowired
    private ApeSpokenService apeSpokenService;
    @Autowired
    private ApeSpokenUserService apeSpokenUserService;

    /** 分页获取口语 */
    @Log(name = "分页获取口语", type = BusinessType.OTHER)
    @PostMapping("getApeSpokenPage")
    public Result getApeSpokenPage(@RequestBody ApeSpoken apeSpoken) {
        Page<ApeSpoken> page = new Page<>(apeSpoken.getPageNumber(),apeSpoken.getPageSize());
        QueryWrapper<ApeSpoken> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(apeSpoken.getContent()),ApeSpoken::getContent,apeSpoken.getContent())
                .like(StringUtils.isNotBlank(apeSpoken.getChinese()),ApeSpoken::getChinese,apeSpoken.getChinese())
                .eq(apeSpoken.getCreateTime() != null,ApeSpoken::getCreateTime,apeSpoken.getCreateTime())
                .orderByDesc(ApeSpoken::getCreateTime);
        Page<ApeSpoken> apeSpokenPage = apeSpokenService.page(page, queryWrapper);
        return Result.success(apeSpokenPage);
    }

    /** 根据id获取口语 */
    @Log(name = "根据id获取口语", type = BusinessType.OTHER)
    @GetMapping("getApeSpokenById")
    public Result getApeSpokenById(@RequestParam("id")String id) {
        ApeSpoken apeSpoken = apeSpokenService.getById(id);
        return Result.success(apeSpoken);
    }

    /** 保存口语 */
    @Log(name = "保存口语", type = BusinessType.INSERT)
    @PostMapping("saveApeSpoken")
    public Result saveApeSpoken(@RequestBody ApeSpoken apeSpoken) {
        boolean save = apeSpokenService.save(apeSpoken);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑口语 */
    @Log(name = "编辑口语", type = BusinessType.UPDATE)
    @PostMapping("editApeSpoken")
    public Result editApeSpoken(@RequestBody ApeSpoken apeSpoken) {
        boolean save = apeSpokenService.updateById(apeSpoken);
        if (save) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除口语 */
    @GetMapping("removeApeSpoken")
    @Log(name = "删除口语", type = BusinessType.DELETE)
    @Transactional(rollbackFor = Exception.class)
    public Result removeApeSpoken(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                apeSpokenService.removeById(id);
                QueryWrapper<ApeSpokenUser> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ApeSpokenUser::getSpokenId,id);
                apeSpokenUserService.remove(queryWrapper);
            }
            return Result.success();
        } else {
            return Result.fail("口语id不能为空！");
        }
    }

}