package com.netease.task;

import com.netease.urs.task.consumer.AbstractConsumerTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: chenzhongzheng
 * @Date: 2017-06-08
 * @Description:
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test.xml")
public class AbstractConsumerTaskTest {

    @Resource
    private AbstractConsumerTask consumerTask;

    @Test
    public void test() throws InterruptedException {

        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);
        queue.put("urstest_czz");
        queue.put("urstest_czz2");

        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("index", "2");

        //读数据
        consumerTask.setRequestMap(requestMap);
        consumerTask.setUsernameQueue(queue);
        consumerTask.run();

    }
}
