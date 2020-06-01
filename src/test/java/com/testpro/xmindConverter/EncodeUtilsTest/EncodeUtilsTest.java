package com.testpro.xmindConverter.EncodeUtilsTest;

import com.testpro.xmindConverter.utils.EncodeUtils;
import org.testng.annotations.Test;

public class EncodeUtilsTest {
    @Test
    public void TestChineseToUnicode(){
        String chineseStr ="接口测试平台";
        String unicode = new EncodeUtils().getChineseToUnicode(chineseStr);
        unicode.replace("\\u","\\\\\\u");
        System.out.println(unicode);
    }
}
