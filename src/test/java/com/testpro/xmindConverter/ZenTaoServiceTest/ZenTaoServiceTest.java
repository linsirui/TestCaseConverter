package com.testpro.xmindConverter.ZenTaoServiceTest;

import com.testpro.xmindConverter.service.ZenTaoService;
import org.apache.commons.configuration.ConfigurationException;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class ZenTaoServiceTest {
    ZenTaoService zenTaoService = new ZenTaoService();

    public ZenTaoServiceTest() throws ConfigurationException {
    }

    @Test
    public void testGetProductDetail() throws ConfigurationException {
        String sid = zenTaoService.loginZentao("admin","123456");
        Map<String,String> moduleList = new HashMap<>();
        int moduleID = zenTaoService.getModuleID(3,"模块1");
        System.out.println(moduleID);
    }
}
