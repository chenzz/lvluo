package com.netease.task.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.BlockingQueue;

/**
 * @Author: chenzhongzheng
 * @Date: 2017-06-08
 * @Description:
 */
public class ProviderTask implements Runnable  {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderTask.class);
    private BlockingQueue<String> queue;
    private String filePath;

    public ProviderTask(BlockingQueue<String> usernameQueue, String filePath) {
        this.queue = usernameQueue;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try (
                InputStream inputStream = new FileInputStream(filePath);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ) {
            String line = null;
            while ((line = (bufferedReader.readLine())) != null) {
                try {
                    queue.put(line);
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            while (queue.size() != 0) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }
    }
}
