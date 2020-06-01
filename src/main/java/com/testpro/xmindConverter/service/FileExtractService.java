package com.testpro.xmindConverter.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.testpro.xmindConverter.domain.TestCase;
import org.apache.commons.configuration.ConfigurationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileExtractService {

    public List<TestCase> getTestCaseList(String fileContent, String productName, String templateType) throws ConfigurationException {
        ZenTaoService zenTaoService = new ZenTaoService();

        JSONArray xmindSheets = JSONArray.parseArray(fileContent); //一个sheet是一个JSONObject
        int xmindSheetCount = xmindSheets.size();
        int productID = zenTaoService.getProductID(productName);

        List<TestCase> testCasesList = new ArrayList<>();

        for(int i = 0; i < xmindSheetCount; i++){

            String storyName,moduleName;


            JSONArray testScenarios = JSONArray.parseArray(JSONPath.eval(xmindSheets.getJSONObject(i),"$rootTopic.children.attached").toString());
            int testScenarioCount = testScenarios.size();
            for(int j = 0; j < testScenarioCount; j++){
                JSONArray caseArray = JSONArray.parseArray(JSONPath.eval(testScenarios.getJSONObject(j),"$.children.attached").toString());
                int caseArraySize = caseArray.size();
                for(int k = 0; k < caseArraySize; k++){
                    JSONObject jsonCase = caseArray.getJSONObject(k);
                    TestCase eachCase = new TestCase();
                    //获取测试用例名称
                    String caseName = JSONPath.eval(jsonCase,"$.title").toString();

                    int storyID = 0,moduleID;
                    if(templateType.equalsIgnoreCase("storyBased")){
                        storyName = JSONPath.eval(xmindSheets.getJSONObject(i),"$rootTopic.title").toString();
                        Map caseConfig = zenTaoService.getStoryList(storyName,productID);
                        storyID = Integer.valueOf(caseConfig.get("StoryID").toString()); //根据需求名称和product id获取需求ID
                        moduleID = Integer.valueOf(caseConfig.get("ModuleID").toString()); }//获取模块ID
                    else {
                        moduleName = JSONPath.eval(xmindSheets.getJSONObject(i),"$rootTopic.title").toString();
                        moduleID = zenTaoService.getModuleID(productID,moduleName);
                    }
                    //获取测试步骤和预期，及是否有前置条件
                    JSONArray caseStepsArray = JSONArray.parseArray(JSONPath.eval(jsonCase,"$.children.attached").toString());
                    List<String> steps = new ArrayList<>();
                    List<String> expected = new ArrayList<>();
                    int caseStepsCount = caseStepsArray.size();
                    for(int m = 0; m < caseStepsCount; m++){
                        JSONObject eachStep = caseStepsArray.getJSONObject(m);
                        if(eachStep.containsKey("markers")){
                            eachCase.setPrecondition(JSONPath.eval(eachStep,"$.title").toString());
                        }else {
                            steps.add(JSONPath.eval(eachStep,"$.title").toString());
                            expected.add(JSONPath.eval(eachStep,"$.children.attached[0].title").toString());

                        }
                    }
                    eachCase.setCaseName(caseName);
                    eachCase.setProductID(productID);
                    if(templateType.equalsIgnoreCase("storyBased")){ //只有当storyBased才是需要加storyID
                        eachCase.setStoryID(storyID);
                    }
                    eachCase.setModuleID(moduleID);
                    eachCase.setSteps(steps);
                    eachCase.setExpected(expected);


                    testCasesList.add(eachCase);
                    System.out.println("*****************");
                    System.out.println(eachCase.getCaseName());
                    System.out.println(eachCase.getPrecondition());
                    System.out.println(eachCase.getSteps());
                    System.out.println(eachCase.getExpected());
                    System.out.println("*****************");
                }

            }


        }
        return testCasesList;
    }
}
