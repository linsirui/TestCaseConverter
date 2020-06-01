package com.testpro.xmindConverter.utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;

public class DoGet_NotUsed {


    /**
     * 只传url进行Get请求并返回response
     * @param url
     * @return 返回请求的response, String类型
     * @throws IOException
     */
    public String DoGetNoParams(String url) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8099/api-getsessionid.json");

        CloseableHttpResponse response = null;
        String responseData = null;
        try{
            response = httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode() == 200){
                responseData = EntityUtils.toString(response.getEntity(),"utf-8");
                System.out.println(responseData);
            }
        }finally {
            if(response != null){
                response.close();
            }
            httpClient.close();
        }
        return responseData;
    }

    public String DoGetWithParams(String url, HashMap params){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        return url;



    }
}
