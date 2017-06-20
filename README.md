批量用户离线处理容器——绿萝

[TOC]

最近工作中涉及到两个对批量用户进行离线处理的工作：

1. 对若干用户进行打标记。
2. 对若干用户进行消息推送。

联想到之前也有很多这种类似的工作，索性把其中共用的部分抽离出来做成了框架——取名叫绿萝。

通过使用该容器，使用者编写处理业务相关的代码，业务无关的部分交给容器来解决。好比 Servlet和Tomcat之间的关系，绿萝作为一个容器来运行业务代码。

## 1、有什么用

具体来说，绿萝主要为使用者处理以下逻辑：

1. 从文件中读取用户列表进行后续处理
2. 把读取的用户列表分发给若干现成进行进行处理，线程数可指定
3. 可以通过参数对消费的QPS进行限制，防止影响线上系统
4. 对所有用户的处理结果进行返回码的统计
5. 把业务参数解析成Map供使用者进行使用



## 2、怎么用

绿萝使用起来很简单，需要以下几步

#### 2.1、引入依赖
```xml
        <repository>
            <id>libs-snapshots</id>
            <name>Server Name-releases</name>
            <url>http://mvn.hz.netease.com/artifactory/libs-snapshots</url>
        </repository>

		<dependency>
            <groupId>com.netease.urs</groupId>
            <artifactId>lvluo-worker</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```
#### 2.2、实现接口

写一个类继承`AbstractConsumerTask`，并添加@Component注解。

（注：该类需要放置在com.netease路径下）



例如，要对1亿用户进行消息推送，那么编写以下类即可：

```java
@Component
public class PushConsumerTask extends AbstractConsumerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(OneConsumerTask.class);
    private static final Logger SUCCESS_LOGGER = LoggerFactory.getLogger("successLogger");
    private static final Logger FAIL_LOGGER = LoggerFactory.getLogger("failLogger");

    private AtomicLong successCount = new AtomicLong(0);
    private AtomicLong failCount = new AtomicLong(0);

    @Override
    public int doService(String username, Map<String, String> requestMap) {

        //获取请求参数，进行参数校验
        String message = requestMap.get("message");
        if (StringUtils.isEmpty(indexStr)) {
            System.out.println("message 参数为空");
            System.exit(-1)
        }

        //业务逻辑处理
        int retCode = pushMessage(username, message);

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
```



#### 2.3、打包

pom.xml中加入以下代码：

其中finalName要指定成想要的名字。

```xml

    <build>
        <finalName>demo</finalName>
        <sourceDirectory>src/main/java</sourceDirectory>

        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>1.4</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                                    <resource>META-INF/spring.handlers</resource>
                                </transformer>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.netease.urs.Main</mainClass>
                                </transformer>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                                    <resource>META-INF/spring.schemas</resource>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

#### 2.4、运行

`java -jar demo.jar 消费任务 文件路径 业务参数 系统参数`

* 消费任务 - 编写的业务类的名称，首字母替换成小写
* 文件路径 - 用户名文件对应的文件路径
* 业务参数 - 你的业务中需要用的参数 （如果有特殊字符或者中文需要进行urlencode）
* 系统参数 - 如threadCount 指定线程数，qps指定限制的频率

如，

`java -jar demo.jar pushConsumerTask usernameList.txt message=xx threadCount=10&qps=100`



#### 2.5、输出结果

控制台输出：

```text
10:32:05,096  INFO DemoConsumerTask:51 - 失败：第504个逻辑处理完成, 用户名为urstest_czz994
10:32:05,096  INFO DemoConsumerTask:48 - 成功：第492个逻辑处理完成, 用户名为urstest_czz995
10:32:05,096  INFO AbstractConsumerTask:92 - 本次请求返回码1,用户名为urstest_czz994。总共请求995次，当前返回码统计结果：{0:491,1:504}
10:32:05,097  INFO AbstractConsumerTask:92 - 本次请求返回码0,用户名为urstest_czz995。总共请求996次，当前返回码统计结果：{0:492,1:504}
```

logs/success.log:

```text
urstest_czz3
urstest_czz4
urstest_czz6
urstest_czz9
urstest_czz10
urstest_czz11
```

logs/fail.log

```
urstest_czz0
urstest_czz1
urstest_czz2
urstest_czz5
urstest_czz7
```



## 3、代码

如果对代码有兴趣欢迎围观吐槽~

ssh://git@g.hz.netease.com:22222/hzchenzz/lvluo.git



工程中包含了两个module：

lvluo-demo 是lvluo的使用demo

lvluo-worker 是lvluo的具体实现