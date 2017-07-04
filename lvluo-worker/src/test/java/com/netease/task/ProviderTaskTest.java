package com.netease.task;

import com.netease.task.provider.ProviderTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: chenzhongzheng
 * @Date: 2017-06-08
 * @Description:
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-test.xml")
public class ProviderTaskTest {

    @Test
    public void testReadFromFile2Queue() throws InterruptedException {

        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);

        //读数据
        ProviderTask providerTask = new ProviderTask(queue, "/Users/chenzhongzheng/URS/job-scripts/mark-user/src/main/resources/usernameList.txt");
        providerTask.run();


        //取数据
        String username = null;
        while ((username = queue.take()) != null) {
            System.out.println(username);
        }
    }
}
