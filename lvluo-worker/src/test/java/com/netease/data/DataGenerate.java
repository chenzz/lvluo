package com.netease.data;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @Author: chenzhongzheng
 * @Date: 2017-06-08
 * @Description:
 */
public class DataGenerate {

    @Test
    public void testGenerateUsername() {

        String filePath = "/Users/chenzhongzheng/URS/job-scripts/mark-user/src/main/resources/usernameList.txt";
        try (
                Writer fileWriter = new FileWriter(filePath);
                Writer writer = new BufferedWriter(fileWriter)
        ) {
            for (int i = 0; i < 1000; i++) {
                String username = "urstest_czz" + i + "\n";
                writer.write(username);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
