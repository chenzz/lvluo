package com.netease.urs;

import com.netease.urs.utils.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: chenzhongzheng
 * @Date: 2017-06-08
 * @Description:
 */
public class Main {

    public static void main(String[] args) {

//        args = new String[] {"markAddConsumerTask", "/Users/chenzhongzheng/URS/job-scripts/mark-user/src/main/resources/usernameList.txt", "index=2", "2"};

        //参数校验
        if (args.length < 4) {
            System.out.println("缺少参数，调用格式如下：");
            System.out.println("java -jar mark.jar 任务名 文件路径 请求参数 [线程数(缺省为1)]");
            System.out.println("如：");
            System.out.println("java -jar mark.jar markAddConsumerTask usernameList.txt index=2 3");
            return;
        }

        String beanName = args[0];
        String filePath = args[1];
        String requestStr = args[2];
        String configStr = args[3];

        Map<String, String> requestMap = StringUtils.convertString2Map(requestStr);
        Map<String, String> configMap = StringUtils.convertString2Map(configStr);


        //业务
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:applicationContext*.xml");

        LvluoApp lvluoApp = applicationContext.getBean(LvluoApp.class);
        lvluoApp.doWork(beanName, filePath, requestMap, configMap);
    }
}
