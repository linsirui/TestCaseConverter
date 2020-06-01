package com.testpro.xmindConverter.HttpUtilsTest;

import com.testpro.xmindConverter.service.ZenTaoService;
import com.testpro.xmindConverter.utils.DoGet_NotUsed;
import org.apache.commons.configuration.ConfigurationException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

public class HttpAPIServiceTest {

    @Test
    public void testHttpPureGet() throws IOException {

        String response = new DoGet_NotUsed().DoGetNoParams("http://localhost:8099/api-getsessionid.json");
        System.out.println(response);

    }

    @Test
    public void testGetSession() throws ConfigurationException {
        RestTemplate restTemplate = new RestTemplate();
        String response = new ZenTaoService().getSession();
        System.out.println(response);
    }



    @Test
    public void testGetSessionID() throws ConfigurationException {
        String sessionID = new ZenTaoService().getSession();
        System.out.println(sessionID);
    }

    @Test
    public void testGetProduct() throws ConfigurationException {
        int productID = new ZenTaoService().getProductID("接口测试平台");
        System.out.println(productID);
    }



    @Test
    public void testGetStoryList() throws ConfigurationException {
        //Map result = new ZenTaoService().getStoryList("模块2-需求1");
        //System.out.println(result.toString());
    }


}
