package com.testpro.xmindConverter.controller;

import com.alibaba.fastjson.JSONObject;
import com.testpro.xmindConverter.domain.TestCase;
import com.testpro.xmindConverter.service.FileExtractService;
import com.testpro.xmindConverter.service.ZenTaoService;
import org.apache.commons.codec.Charsets;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static org.apache.tomcat.jni.Time.now;

@CrossOrigin
@RestController
public class FileController {

    @PostMapping("/upload")
    @ResponseBody
    public JSONObject fileUpload(@RequestParam("file")MultipartFile srcFile,@RequestParam("product") String product,@RequestParam("UserName") String userName, @RequestParam("password") String password,@RequestParam("templateType") String templateType ) throws IOException, ConfigurationException {
        System.out.println("the template type is: "+templateType);
        JSONObject response = new JSONObject();
        if(srcFile.isEmpty()){
            System.out.println("请检查文件为空");
            response.put("msg","error");
            response.put("code",9999);

        }else {
        InputStream fileInputStream = srcFile.getInputStream();
        String fileContent = IOUtils.toString(fileInputStream, Charsets.UTF_8);
        Long time1 = new Date().getTime();

        List<TestCase> testCases = new FileExtractService().getTestCaseList(fileContent,product,templateType);
        int caseCount = testCases.size();
        ZenTaoService zenTaoService = new ZenTaoService();
        String sessionID = zenTaoService.loginZentao(userName,password);
        for (int i = 0; i < caseCount; i++){
            zenTaoService.addTestCase(testCases.get(i),templateType);
        }
        Long time2 = new Date().getTime();
        Long secs = time2 - time1;
        System.out.println("time cost is "+secs );
        response.put("msg","success");
        response.put("code",0);
        response.put("product",product);
        response.put("data",fileContent);

       }
        return response;
    }


    @PostMapping("/uploadFile")
    @ResponseBody
    public JSONObject fileUpload(@RequestParam("file")MultipartFile srcFile) throws IOException, ConfigurationException {
        JSONObject response = new JSONObject();
        if(srcFile.isEmpty()){
            System.out.println("请检查文件为空");
            response.put("msg","error");
            response.put("code",9999);

        }else {
            InputStream fileInputStream = srcFile.getInputStream();
            String fileContent = IOUtils.toString(fileInputStream, Charsets.UTF_8);
            response.put("msg","success");
            response.put("code",0);
            response.put("data",fileContent);
        }
        return response;
    }
}
