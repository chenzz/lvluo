package com.netease.app;

import com.netease.LvluoApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: chenzhongzheng
 * @Date: 2017-06-08
 * @Description:
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test.xml")
public class AppTest {

    @Resource
    private LvluoApp lvluoApp;

    @Test
    public void testDemoConsumerTask() {

        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("index", "2");

        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put("threadCount", "2");
        configMap.put("qps", "10");

        lvluoApp.doWork("demoConsumerTask", "/Users/chenzhongzheng/URS/job-scripts/lvluo/lvluo-worker/src/test/resources/usernameList.txt", requestMap, configMap);
    }
}

