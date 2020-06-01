package com.testpro.xmindConverter.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.testpro.xmindConverter.domain.TestCase;
import com.testpro.xmindConverter.utils.HttpRequestUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;



public class ZenTaoService {

    String relativelyPath = System.getProperty("user.dir");//获取当前项目的根目录
    PropertiesConfiguration config = new PropertiesConfiguration(relativelyPath+"\\src\\main\\resources\\application.properties");

    public ZenTaoService() throws ConfigurationException {
    }

    public String getSession() throws ConfigurationException {
        String body = new HttpRequestUtils().DoGet("http://localhost:8099/api-getsessionid.json").getBody().toString();
        JSONObject jsonObject = JSONObject.parseObject(body);
        String sessionID = JSONObject.parseObject(jsonObject.getString("data")).getString("sessionID");
        //将获取到的session id存储到properties文件中并保存

        config.setProperty("zentaosid",sessionID);
        config.save();
        return sessionID;
    }

    @RequestMapping(value = "/zentaoLogin", method = RequestMethod.GET)
    public String loginZentao(String userName, String password) throws ConfigurationException {
        String sessionID = getSession();
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        //TO DO 将header中的值加入配置文件
        headers.add("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
        headers.add("Content-Type","application/x-www-form-urlencoded");
        headers.add("Referer","http://localhost:8099/zentaopms/www/user-login.html");
        String tempCookie ="zentaosid=tobereplaced; path=/".replace("tobereplaced",getSessionFromConfig());
        headers.add(HttpHeaders.COOKIE,tempCookie);
        parameters.add("account",userName);
        parameters.add("password",password);
        parameters.add("zentaosid",getSessionFromConfig());
        ResponseEntity loginResult = new HttpRequestUtils().DoPostParameters("http://localhost:8099/user-login.json",headers,null,parameters);
        System.out.println(loginResult.getBody());
        System.out.println(loginResult.getHeaders().get("Set-Cookie").get(0));
        System.out.println(loginResult.getHeaders().get("Set-Cookie").get(1));
        return sessionID;
    }

    public void addTestCase(TestCase testCase, String fileType) throws ConfigurationException {

        //loginZentao(userName,password);
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        LinkedMultiValueMap<String,String> parameters = new LinkedMultiValueMap<String, String>();
        LinkedMultiValueMap<String, String> cookies = new LinkedMultiValueMap<String, String>();

        //headers.add("Content-Type","multipart/form-data");
        //从TestCase中获取product id, module id, story id
        int caseProduct = testCase.getProductID();
        int caseModule = testCase.getModuleID();
        int caseStory =0;
        if(fileType.equalsIgnoreCase("storyBased")){
            caseStory = testCase.getStoryID();
        }


        //拼接创建的url. referer比加，不然创建不成功
        String baseURL ="http://localhost:8099/testcase-create-[PRODUCTID]-0-[MODULEID]";//TO DO 加入配置文件
        baseURL = baseURL.replace("[PRODUCTID]",String.valueOf(caseProduct));
        baseURL = baseURL.replace("[MODULEID]",String.valueOf(caseModule));
        //baseURL = baseURL.replace("[STORYID]",String.valueOf(caseStory));

        headers.add("Referer",baseURL+".html");
        headers.add("Content-Type","multipart/form-data; boundary=5VmKhITpDW0N44RZ6D2NfR6gqpKt4PbUT");
        headers.add("User-Agent","Apache-HttpClient/4.5.7 (Java/1.8.0_231)");
        headers.add("Host","localhost:8099");//TO DO 加入配置文件
        //headers.add(HttpHeaders.COOKIE,"zentaosid="+sessionID);
        String tempCookie = "zentaosid=tobereplaced; path=/".replace("tobereplaced",getSessionFromConfig());
        headers.add(HttpHeaders.COOKIE,tempCookie);

        parameters.add("product",String.valueOf(caseProduct));
        parameters.add("module",String.valueOf(caseModule));
        parameters.add("type","feature");
        parameters.add("stage","feature");
        if(fileType.equalsIgnoreCase("storyBased")){
            parameters.add("story",String.valueOf(caseStory));}
        parameters.add("title",testCase.getCaseName());
        //判断是否有precondition
        if(testCase.getPrecondition() == null){
            parameters.add("precondition","");
        }else {
            parameters.add("precondition",testCase.getPrecondition());
        }
        //组装Steps和Expected

        int stepsCount = testCase.getSteps().size();
        for (int i = 0; i < stepsCount; i++){
            parameters.add("steps[index]".replace("index",String.valueOf(i+1)),testCase.getSteps().get(i));// zentao中，index是从1开始
            parameters.add("expects[index]".replace("index",String.valueOf(i+1)),testCase.getExpected().get(i));// zentao中，index是从1开始
        }



        //cookies.add("zentaosid",getSessionFromConfig());
        //cookies.add("lang","zh-cn");
        //cookies.add("device","desktop");
        //cookies.add("theme","default");
        System.out.println("添加test case的url是："+baseURL+".json");
        System.out.println("添加test case的参数是: "+parameters.toString());
        ResponseEntity result = new HttpRequestUtils().DoPostParametersForm(baseURL+".json",headers,null,parameters);
        System.out.println(result.getBody().toString());
        System.out.println(result.getHeaders().toString());
        //System.out.println(result.toString());

    }

    /**
     * 从application.properties文件中获取已经认证过的session id
     * @return
     */
    private String getSessionFromConfig(){
        Boolean keyExisted = config.containsKey("zentaosid");
        String sessionID = null;
        if(keyExisted == true){
            //System.out.println("key existed");
            sessionID = config.getString("zentaosid");
        }else {
            System.out.println("key not existed");
        }
        return sessionID;
    }

    public Integer getProductID(String productName){
        String sesssionID = getSessionFromConfig();
        ResponseEntity responseEntity = new HttpRequestUtils().DoGet("http://localhost:8099/product-index-no.json");
        String data = JSONObject.parseObject(responseEntity.getBody().toString()).getString("data");
        JSONObject productList = JSONObject.parseObject(data).getJSONObject("products");
        Set<String> keyList = productList.keySet();
        String finalKey = "";
        int keyListSize = keyList.size();
        for(String key:keyList){
            String eachProduct = productList.getString(key);
            if(productName.trim().equalsIgnoreCase(eachProduct)){
                finalKey = key;
                break;
            }
        }
        return Integer.parseInt(finalKey);

    }

    public Map getStoryList(String storyName, int productID){
        String sessionId = getSessionFromConfig();
        String storyURL = "http://localhost:8099/product-browse-[productID].json".replace("[productID]",String.valueOf(productID));
        ResponseEntity responseEntity = new HttpRequestUtils().DoGet(storyURL);
        String response = responseEntity.getBody().toString();
        String data = JSONObject.parseObject(response).getString("data");
        JSONArray storyListArray = JSONArray.parseArray(JSONObject.parseObject(data).getString("stories"));
        int storyListSize = storyListArray.size();

        Map resultStory = new HashMap();
        for(int i = 0; i<storyListSize; i++){
            JSONObject eachStory = storyListArray.getJSONObject(i);
            if(storyName.trim().equalsIgnoreCase(eachStory.getString("title"))){
                resultStory.put("StoryID",eachStory.getString("id"));
                resultStory.put("ModuleID",eachStory.getString("module"));
                break;
            }
        }
        if(resultStory.isEmpty()){
            System.out.println("The story provided not found");
        }
        return resultStory;
    }

    public int getModuleID(int productID, String moduleName){

        String sessionId = getSessionFromConfig();
        String storyURL = "http://localhost:8099/product-browse-[productID].json".replace("[productID]",String.valueOf(productID));
        ResponseEntity responseEntity = new HttpRequestUtils().DoGet(storyURL);
        String response = responseEntity.getBody().toString();
        String data = JSONObject.parseObject(response).getString("data");
        JSONObject dataObject = JSONObject.parseObject(data);
        JSONObject moduleObject = dataObject.getJSONObject("modules");
   
        int moduleID = 0;
        Set<String> keySet = moduleObject.keySet();
        for(String key : keySet){
            String keyValue = moduleObject.getString(key); // {"0":"/","11":"/模块1","12":"/模块2","13":"/模块3"}
            if("/".concat(moduleName).equalsIgnoreCase(keyValue)){
                //System.out.println(key);
                moduleID = Integer.valueOf(key);
            }
        }
        return moduleID;
    }
}
