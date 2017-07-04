package com.netease.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: chenzhongzheng
 * @Date: 2017-06-18
 * @Description:
 */
public class StringUtils {

    public static Map<String, String> convertString2Map(String requestStr) {
        Map<String, String> requestMap = new HashMap<String, String>();


        //把 a=b&c=d 的字符串转换成Map
        if (!org.apache.commons.lang.StringUtils.isEmpty(requestStr)) {
            String[] requestArr = requestStr.split("&");

            for (String str : requestArr) {
                String[] kv = str.split("=");

                String key = null;
                String value = null;
                try {
                    key = URLDecoder.decode(kv[0], "UTF-8");
                    if (kv.length >= 2) {
                        value = URLDecoder.decode(kv[1], "UTF-8");
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                requestMap.put(key, value);
            }
        }

        return requestMap;
    }
}
