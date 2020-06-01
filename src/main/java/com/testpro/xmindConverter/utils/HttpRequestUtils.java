package com.testpro.xmindConverter.utils;

import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HttpRequestUtils {

    /**
     * 发送一个HTTP GET请求，不带参数，只有url
     * @param url
     * @return 接口返回的内容
     */
    public ResponseEntity DoGet(String url){
        RestTemplate template = new RestTemplate();
        if(url == null){
            //TO DO add error log
        }
        ResponseEntity<String> result = template.getForEntity(url,String.class);
        return result;
    }

    public ResponseEntity DoPostParameters(String url, MultiValueMap<String, String> headers, MultiValueMap<String, String> cookies, MultiValueMap<String, String> params){
        RestTemplate template = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addAll(headers);
        List<String> httpCookies =new ArrayList<String>();
        if(cookies!= null){
            Set<String> cookieKeyList = cookies.keySet();
            for(String cookieKey : cookieKeyList){
                httpCookies.add(cookieKey + "=" + cookies.get(cookieKey));
            }
            httpHeaders.put(HttpHeaders.COOKIE,httpCookies);
        }
        //httpCookies.add("zentaosid=661k001519uet5be2m67rd5356; lang=zh-cn; device=desktop; theme=default");
        //httpHeaders.put(HttpHeaders.COOKIE,httpCookies);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params,httpHeaders);
        ResponseEntity response = template.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return response;
    }

    public ResponseEntity DoPostParametersForm(String url, MultiValueMap<String, String> headers, MultiValueMap<String, String> cookies, MultiValueMap<String, String> params){
        RestTemplate restTemplateMultiForm = new RestTemplate(new SimpleClientHttpRequestFactory());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addAll(headers);
        //httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        List<String> httpCookies =new ArrayList<String>();
        if(cookies!= null){
            Set<String> cookieKeyList = cookies.keySet();
            for(String cookieKey : cookieKeyList){
                httpCookies.add(cookieKey + "=" + cookies.get(cookieKey));
            }
            httpHeaders.put(HttpHeaders.COOKIE,httpCookies);
        }
        //List<String> tempCookie = new ArrayList<>();
        //tempCookie.add("zentaosid=a7usert87t0qbmt1l8aifs7g60");

        //httpHeaders.put(HttpHeaders.COOKIE, tempCookie);

        //httpHeaders.SET_COOKIE.

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params,httpHeaders);

        ResponseEntity response = restTemplateMultiForm.exchange(url, HttpMethod.POST, requestEntity, String.class);
        //String response = restTemplateMultiForm.postForObject(url,requestEntity,String.class);

        return response;
    }



}
