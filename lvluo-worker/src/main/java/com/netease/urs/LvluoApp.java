package com.netease.urs;

import com.netease.urs.task.consumer.AbstractConsumerTask;
import com.netease.urs.task.provider.ProviderTask;
import com.netease.urs.utils.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: chenzhongzheng
 * @Date: 2017-06-08
 * @Description:
 */

@Component
public class LvluoApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(LvluoApp.class);

    public void doWork(String beanName, String filePath, Map<String, String> requestMap, Map<String, String> configMap) {
        final BlockingQueue<String> usernameQueue = new LinkedBlockingDeque<>(10000);

        //读用户名到队列中
        ExecutorService providerExecutorService = Executors.newSingleThreadExecutor();
        ProviderTask providerTask = new ProviderTask(usernameQueue, filePath);
        providerExecutorService.submit(providerTask);

        //消费队列中的用户名
        int threadCount = 1;
        if (configMap != null && configMap.get("threadCount") != null) {
            threadCount = Integer.parseInt(configMap.get("threadCount"));
        }
        ExecutorService consumerExecutorService = Executors.newFixedThreadPool(threadCount);

        AbstractConsumerTask consumerTask = (AbstractConsumerTask) ApplicationContextProvider.getBean(beanName);
        consumerTask.setUsernameQueue(usernameQueue);
        consumerTask.setRequestMap(requestMap);
        consumerTask.setConfigMap(configMap);

        List<Future> futureList = new ArrayList<Future>();
        for (int i = 0; i < threadCount; i++) {
            Future future = consumerExecutorService.submit(consumerTask);
            futureList.add(future);
        }

        providerExecutorService.shutdown();
        consumerExecutorService.shutdown();


        //当provider任务结束 且 queue 中被消费完时，中断consumer线程
        while (true) {
            if (providerExecutorService.isTerminated() && usernameQueue.size() == 0) {
                for (Future future : futureList) {
                    future.cancel(true);
                }
                break;
            } else {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

    }
}
