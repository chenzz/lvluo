package com.netease.urs;

import com.netease.urs.task.consumer.AbstractConsumerTask;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: chenzhongzheng
 * @Date: 2017-06-15
 * @Description:
 */
@Component
public class OneConsumerTask extends AbstractConsumerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(OneConsumerTask.class);
    private static final Logger SUCCESS_LOGGER = LoggerFactory.getLogger("successLogger");
    private static final Logger FAIL_LOGGER = LoggerFactory.getLogger("failLogger");

    private AtomicLong successCount = new AtomicLong(0);
    private AtomicLong failCount = new AtomicLong(0);

    @Override
    public int doService(String username, Map<String, String> requestMap) {

        //获取请求参数，进行参数校验
        String indexStr = requestMap.get("index");
        if (StringUtils.isEmpty(indexStr)) {
            System.out.println("index 参数为空");
        }
        int index = Integer.parseInt(indexStr);

        //业务逻辑处理
        System.out.println(username + "的逻辑处理完成了~");

        Random random = new Random();
        int retCode = random.nextInt(2);

        //打印需要的结果到日志
        if (retCode == 0) {
            SUCCESS_LOGGER.info(username);
            LOGGER.info(MessageFormat.format("成功：第{0}个逻辑处理完成, 用户名为{1}", successCount.addAndGet(1), username));
        } else {
            FAIL_LOGGER.info(username);
            LOGGER.info(MessageFormat.format("失败：第{0}个逻辑处理完成, 用户名为{1}", failCount.addAndGet(1), username));
        }

        return retCode;
    }
}
