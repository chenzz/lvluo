package com.netease.urs.task.consumer;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: chenzhongzheng
 * @Date: 2017-06-08
 * @Description:
 */

public abstract class AbstractConsumerTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConsumerTask.class);

    private BlockingQueue<String> usernameQueue;
    private ConcurrentHashMap<Integer, AtomicLong> retCodeHashMap = new ConcurrentHashMap<Integer, AtomicLong>();
    private AtomicLong totalCount = new AtomicLong(0);
    private Map<String, String> requestMap;
    private Map<String, String> configMap;
    private volatile RateLimiter rateLimiter;

    public void setUsernameQueue(BlockingQueue<String> usernameQueue) {
        this.usernameQueue = usernameQueue;
    }

    public void setRequestMap(Map<String, String> requestMap) {
        this.requestMap = requestMap;
    }

    public void setConfigMap(Map<String, String> configMap) {
        this.configMap = configMap;
    }

    @Override
    public void run() {

        //单例初始化 RateLimiter
        String rateLimitStr = configMap.get("qps");
        if (!StringUtils.isEmpty(rateLimitStr) && rateLimiter == null) {
            synchronized (this.getClass()) {
                if (rateLimiter == null) {
                    int rateLimit = Integer.parseInt(rateLimitStr);
                    rateLimiter = RateLimiter.create(rateLimit);
                }
            }
        }

        String username = null;
        try {
            while ((username = usernameQueue.take()) != null) {

                if (rateLimiter != null) {
                    rateLimiter.acquire();
                }

                int retCode = doService(username, requestMap);

                //统计返回码
                totalCount.incrementAndGet();
                AtomicLong retCodeCount = retCodeHashMap.get(retCode);
                if (retCodeCount == null) {
                    synchronized (retCodeHashMap) {
                        retCodeCount = retCodeHashMap.get(retCode);

                        if (retCodeCount == null) {
                            retCodeHashMap.put(retCode, new AtomicLong(1L));
                        } else {
                            retCodeCount = retCodeHashMap.get(retCode);
                            retCodeCount.incrementAndGet();
                        }
                    }
                } else {
                    retCodeCount = retCodeHashMap.get(retCode);
                    retCodeCount.incrementAndGet();
                }

                LOGGER.info(MessageFormat.format("本次请求返回码{0},用户名为{1}。总共请求{2}次，当前返回码统计结果：{3}", retCode, username, totalCount.get(), JSON.toJSONString(retCodeHashMap)));
            }
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public abstract int doService(String username, Map<String, String> requestMap);

}
