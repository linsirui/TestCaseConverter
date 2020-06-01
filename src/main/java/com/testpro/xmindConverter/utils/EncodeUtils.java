package com.testpro.xmindConverter.utils;

public class EncodeUtils {

    public String getChineseToUnicode(String chineseStr){
        char[] utfBytes = chineseStr.toCharArray();
        String unicodeBytes ="";
        for(int i = 0; i<utfBytes.length;i++){
            String hexB = Integer.toHexString(utfBytes[i]);
            if(hexB.length()<=2){
                hexB ="00"+hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    public String getUnicodeToChinese(String unicodeStr){
        int start = 0;
        int end = 0;
        StringBuffer buffer = new StringBuffer();
        while(start > -1){
            end = unicodeStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = unicodeStr.substring(start + 2, unicodeStr.length());
            } else {
                charStr = unicodeStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();

    }
}
