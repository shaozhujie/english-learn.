package com.ape.apeadmin.controller.common;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.domain.Result;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.*;
import com.ape.apesystem.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: TODO
 * @date 2023/8/28 11:35
 */
@Controller
@ResponseBody
@RequestMapping("common")
public class CommonController {

    /**
    * @description: 错误转发地址
    * @param: code
    	msg
    * @return:
    * @author shaozhujie
    * @date: 2023/9/14 15:05
    */
    @GetMapping("/error/{code}/{msg}")
    public Result error (@PathVariable("code")Integer code, @PathVariable("msg") String msg){
        return Result.alert(code,msg);
    }

    /**
     * @description: 上传图片
     * @param: file
     * @return:
     * @author shaozhujie
     * @date: 2023/10/13 10:44
     */
    @PostMapping("uploadImg")
    public Result uploadImg(@RequestParam("file") MultipartFile img) {
        if(img.isEmpty()){
            return Result.fail("上传的图片不能为空!");
        }
        String coverType = img.getOriginalFilename().substring(img.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        if ("jpeg".equals(coverType)  || "gif".equals(coverType) || "png".equals(coverType) || "bmp".equals(coverType)  || "jpg".equals(coverType)) {
            int index = img.getOriginalFilename().lastIndexOf(".");
            String substring = img.getOriginalFilename().substring(index);
            String fileName = System.currentTimeMillis() + substring;
            //文件路径
            String filePath = System.getProperty("user.dir")+System.getProperty("file.separator")+"img";
            //如果文件路径不存在，新增该路径
            File file1 = new File(filePath);
            if(!file1.exists()){
                boolean mkdir = file1.mkdir();
            }
            //实际的文件地址
            File dest = new File(filePath + System.getProperty("file.separator") + fileName);
            //存储到数据库里的相对文件地址
            String storeImgPath = "/img/"+fileName;
            try {
                img.transferTo(dest);
                return Result.success(storeImgPath);
            } catch (IOException e) {
                return Result.fail("上传失败");
            }
        } else {
            return Result.fail("请选择正确的图片格式");
        }
    }

    /**
     * @description: 上传视频
     * @param: file
     * @return:
     * @author shaozhujie
     * @date: 2023/10/13 10:44
     */
    @PostMapping("uploadVideo")
    public Result uploadVideo(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()){
            return Result.fail("上传的视频不能为空!");
        }
        int index = file.getOriginalFilename().lastIndexOf(".");
        String substring = file.getOriginalFilename().substring(index);
        String fileName = System.currentTimeMillis() + substring;
        //文件路径
        String filePath = System.getProperty("user.dir")+System.getProperty("file.separator")+"video";
        //如果文件路径不存在，新增该路径
        File file1 = new File(filePath);
        if(!file1.exists()){
            boolean mkdir = file1.mkdir();
        }
        //实际的文件地址
        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        //存储到数据库里的相对文件地址
        String storeVideoPath = "/video/"+fileName;
        try {
            file.transferTo(dest);
            return Result.success(storeVideoPath);
        } catch (IOException e) {
            return Result.fail("上传失败");
        }
    }

    /**
    * @description: 上传文件
    * @param: file
    * @return:
    * @author shaozhujie
    * @date: 2023/10/13 10:44
    */
    @PostMapping("uploadFile")
    public Result uploadFile(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()){
            return Result.fail("上传的文件不能为空!");
        }
        int index = file.getOriginalFilename().lastIndexOf(".");
        String substring = file.getOriginalFilename().substring(index);
        String fileName = System.currentTimeMillis() + substring;
        //文件路径
        String filePath = System.getProperty("user.dir")+System.getProperty("file.separator")+"file";
        //如果文件路径不存在，新增该路径
        File file1 = new File(filePath);
        if(!file1.exists()){
            boolean mkdir = file1.mkdir();
        }
        //实际的文件地址
        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        //存储到数据库里的相对文件地址
        String storeFilePath = "/file/"+fileName;
        try {
            file.transferTo(dest);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("path",storeFilePath);
            jsonObject.put("name",fileName);
            return Result.success(jsonObject);
        } catch (IOException e) {
            return Result.fail("上传失败");
        }
    }

    @Autowired
    private ApeVocabularyUserService apeVocabularyUserService;
    @Autowired
    private ApeSyntaxUserService apeSyntaxUserService;
    @Autowired
    private ApeSpokenUserService apeSpokenUserService;
    @Autowired
    private ApeLiteratureUserService apeLiteratureUserService;
    @Autowired
    private ApeExamineUserService apeExamineUserService;

    @GetMapping("/getRecord")
    public Result getRecord() {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<String> dates = new ArrayList<>();
        List<Integer> cihuiCount = new ArrayList<>();
        List<Integer> cihuiTotalCount = new ArrayList<>();

        List<Integer> yufaCount = new ArrayList<>();
        List<Integer> yufaTotalCount = new ArrayList<>();

        List<Integer> kouyuCount = new ArrayList<>();
        List<Integer> kouyuTotalCount = new ArrayList<>();

        List<Integer> wenxianCount = new ArrayList<>();
        List<Integer> wenxianTotalCount = new ArrayList<>();

        List<Integer> ziceCount = new ArrayList<>();
        List<Integer> ziceTotalCount = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            String date = currentDate.minusDays(i).format(formatter);
            dates.add(date);
            QueryWrapper<ApeVocabularyUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ApeVocabularyUser::getUserId,userInfo.getId())
                    .like(ApeVocabularyUser::getCreateTime,date);
            cihuiCount.add(apeVocabularyUserService.count(queryWrapper));
            QueryWrapper<ApeVocabularyUser> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.lambda().eq(ApeVocabularyUser::getUserId,userInfo.getId())
                    .le(ApeVocabularyUser::getCreateTime,date + " 23:59:59");
            cihuiTotalCount.add(apeVocabularyUserService.count(queryWrapper1));

            QueryWrapper<ApeSyntaxUser> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.lambda().eq(ApeSyntaxUser::getUserId,userInfo.getId())
                    .like(ApeSyntaxUser::getCreateTime,date);
            yufaCount.add(apeSyntaxUserService.count(queryWrapper2));
            QueryWrapper<ApeSyntaxUser> queryWrapper3 = new QueryWrapper<>();
            queryWrapper3.lambda().eq(ApeSyntaxUser::getUserId,userInfo.getId())
                    .le(ApeSyntaxUser::getCreateTime,date + " 23:59:59");
            yufaTotalCount.add(apeSyntaxUserService.count(queryWrapper3));

            QueryWrapper<ApeSpokenUser> queryWrapper4 = new QueryWrapper<>();
            queryWrapper4.lambda().eq(ApeSpokenUser::getUserId,userInfo.getId())
                    .like(ApeSpokenUser::getCreateTime,date);
            kouyuCount.add(apeSpokenUserService.count(queryWrapper4));
            QueryWrapper<ApeSpokenUser> queryWrapper5 = new QueryWrapper<>();
            queryWrapper5.lambda().eq(ApeSpokenUser::getUserId,userInfo.getId())
                    .le(ApeSpokenUser::getCreateTime,date + " 23:59:59");
            kouyuTotalCount.add(apeSpokenUserService.count(queryWrapper5));

            QueryWrapper<ApeLiteratureUser> queryWrapper6 = new QueryWrapper<>();
            queryWrapper6.lambda().eq(ApeLiteratureUser::getUserId,userInfo.getId())
                    .like(ApeLiteratureUser::getCreateTime,date);
            wenxianCount.add(apeLiteratureUserService.count(queryWrapper6));
            QueryWrapper<ApeLiteratureUser> queryWrapper7 = new QueryWrapper<>();
            queryWrapper7.lambda().eq(ApeLiteratureUser::getUserId,userInfo.getId())
                    .le(ApeLiteratureUser::getCreateTime,date + " 23:59:59");
            wenxianTotalCount.add(apeLiteratureUserService.count(queryWrapper7));

            QueryWrapper<ApeExamineUser> queryWrapper8 = new QueryWrapper<>();
            queryWrapper8.lambda().eq(ApeExamineUser::getUserId,userInfo.getId())
                    .like(ApeExamineUser::getCreateTime,date);
            ziceCount.add(apeExamineUserService.count(queryWrapper8));
            QueryWrapper<ApeExamineUser> queryWrapper9 = new QueryWrapper<>();
            queryWrapper9.lambda().eq(ApeExamineUser::getUserId,userInfo.getId())
                    .le(ApeExamineUser::getCreateTime,date + " 23:59:59");
            ziceTotalCount.add(apeExamineUserService.count(queryWrapper9));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",dates);
        jsonObject.put("cihui",cihuiCount);
        jsonObject.put("cihuiTotal",cihuiTotalCount);
        jsonObject.put("yufa",yufaCount);
        jsonObject.put("yufaTotal",yufaTotalCount);
        jsonObject.put("kouyu",kouyuCount);
        jsonObject.put("kouyuTotal",kouyuTotalCount);
        jsonObject.put("wenxian",wenxianCount);
        jsonObject.put("wenxianTotal",wenxianTotalCount);
        jsonObject.put("zice",ziceCount);
        jsonObject.put("ziceTotal",ziceTotalCount);
        return Result.success(jsonObject);
    }

    @Autowired
    private ApeVocabularyService apeVocabularyService;
    @Autowired
    private ApeSyntaxService apeSyntaxService;
    @Autowired
    private ApeSpokenService apeSpokenService;
    @Autowired
    private ApeLiteratureService apeLiteratureService;
    @Autowired
    private ApeUserService apeUserService;
    @Autowired
    private ApeExamineService apeExamineService;
    @Autowired
    private ApeDiscussService apeDiscussService;
    @Autowired
    private ApeDiscussItemService apeDiscussItemService;

    @GetMapping("getIndexData")
    public Result getIndexData() {
        JSONObject jsonObject = new JSONObject();
        int cihui = apeVocabularyService.count();
        int yufa = apeSyntaxService.count();
        int kouyu = apeSpokenService.count();
        int wenxian = apeLiteratureService.count();
        int user = apeUserService.count();
        int zice = apeExamineService.count();
        int taolun = apeDiscussService.count();
        int pinglun = apeDiscussItemService.count();
        jsonObject.put("cihui",cihui);
        jsonObject.put("yufa",yufa);
        jsonObject.put("kouyu",kouyu);
        jsonObject.put("wenxian",wenxian);
        jsonObject.put("user",user);
        jsonObject.put("zice",zice);
        jsonObject.put("taolun",taolun);
        jsonObject.put("pinglun",pinglun);
        return Result.success(jsonObject);
    }

}
