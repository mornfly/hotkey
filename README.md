# hotkey
![输入图片说明](https://images.gitee.com/uploads/images/2020/0616/105737_e5b876cd_303698.png "redis热key探测及缓存到JVM (1).png")

京东APP后台热数据探测框架，历经多次高压压测和2020年京东618大促考验。在上线运行的这段时间内，每天探测的key数量数十亿计，精准捕获了大量爬虫、刷子用户，另准确探测大量热门商品并毫秒级推送到各个服务端内存，大幅降低了热数据对数据层的查询压力，提升了应用性能。

该框架历经多次压测，8核单机worker端每秒可接收处理16万个key探测任务，16核单机至少每秒平稳处理20万以上，实际压测达到30万以上，CPU平稳支撑，框架无异常。在真实业务场景中，可用1：1000的比例，即1台worker支撑1000台服务端Tomcat的key探测任务，即可带来极大的数据存储资源节省（如对redis集群的扩充）。测试详情可去我[CSDN博客](https://blog.csdn.net/tianyaleixiaowu)查看。

#### 架构设计
 **见京东零售公众号** 
https://mp.weixin.qq.com/s/xOzEj5HtCeh_ezHDPHw6Jw

#### 参与开发
该框架的开发得到了来自于：

京东数科-生活服务研发组-李云峰的大力支持，完成了整个dashboard控制台的研发。

京东零售-平台业务中心-APP后台架构师 杜晓宇的大力支持，参与并帮助解决了诸多技术难题。

另有京东集团多个部门提供的意见、建议，和对框架的见解、优化方案。

在此一起感谢。

#### 介绍
对任意突发性的无法预先感知的热点请求，包括并不限于热点数据（如突发大量请求同一个商品）、热用户（如爬虫、刷子）、热接口（突发海量请求同一个接口）等，进行毫秒级精准探测到。
然后对这些热数据、热用户等，推送到该应用部署的所有机器JVM内存中，以大幅减轻对后端数据存储层的冲击，并可以由客户端决定如何使用这些热key（譬如对热商品做本地缓存、对热用户进行拒绝访问、对热接口进行熔断或返回默认值）。
这些热key在整个应用集群内保持一致性。

核心功能：热数据探测并推送至集群各个服务器

适用场景：

1 mysql热数据本地缓存

2 redis热数据本地缓存

3 黑名单用户本地缓存

4 爬虫用户限流

5 接口、用户维度限流

6 单机接口、用户维度限流限流

7 集群用户维度限流

8 集群接口维度限流


#### 尚未完成
文档有待完善。


该开源项目战略意义重大，经历百万级并发，参与京东开源中间件项目建设，一直在等你。

#### worker端强悍的性能表现
每10秒打印一行，totalDealCount代表处理过的key总量，可以看到每10秒处理量在270万-310万之间，对应每秒30万左右QPS。

仅需要很少的机器，即可完成海量key的实时探测计算推送任务。比扩容redis集群规模成本低太多。
![输入图片说明](https://images.gitee.com/uploads/images/2020/0611/152336_78597937_303698.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0611/152249_4ac01178_303698.png "屏幕截图.png")


### 界面效果
![输入图片说明](https://images.gitee.com/uploads/images/2020/0622/163805_0aa68d4b_303698.png "屏幕截图.png")
### 微信群
![输入图片说明](https://images.gitee.com/uploads/images/2020/0720/110150_fee90b3e_303698.jpeg "11.jpeg")


#### 安装教程

1.  安装etcd

    在etcd下载页面下载对应操作系统的etcd，https://github.com/etcd-io/etcd/releases 使用3.4.x以上。相关搭建细节，及常见问题会发布到CSDN博客内。

2.  启动worker（集群）
    下载并编译好代码，将worker打包为jar，启动即可。如：

     **` java -jar $JAVA_OPTS worker-0.0.1-SNAPSHOT.jar --etcd.server=${etcdServer}`** 

    worker可供配置项如下：
![输入图片说明](https://images.gitee.com/uploads/images/2020/0622/164514_c57d740a_303698.png "屏幕截图.png")
    etcdServer为etcd集群的地址，用逗号分隔

    JAVA_OPTS是配置的JVM相关，可根据实际情况配置

    threadCount为处理key的线程数，不指定时由程序来计算。

    workerPath代表该worker为哪个应用提供计算服务，譬如不同的应用appName需要用不同的worker进行隔离，以避免资源竞争。

3.  启动控制台
    
    下载并编译好dashboard项目，创建数据库并导入resource下db.sql文件。 配置一下application.yml里的数据库相关和etcdServer地址。

    启动dashboard项目，访问ip:8081，即可看到界面。

    其中节点信息里，即是当前已启动的worker列表。

    规则配置就是为各app设置规则的地方，初次使用时需要先添加APP。在用户管理菜单中，添加一个新用户，设置他的APP名字，如sample。之后新添加的这个用户就可以登录dashboard给自己的APP设置规则了，登录密码默认123456。
![输入图片说明](https://images.gitee.com/uploads/images/2020/0622/175255_e1b05b4c_303698.png "屏幕截图.png")

    如图就是一组规则，譬如其中as__开头的热key的规则就是interval-2秒内出现了threshold-10次就认为它是热key，它就会被推送到jvm内存中，并缓存60秒，prefix-true代表前缀匹配。那么在应用中，就可以把一组key，都用as__开头，用来探测。

4.  client端接入使用

    引入client的pom依赖。

    在应用启动的地方初始化HotKey，譬如

```
@PostConstruct

public void initHotkey() {

    ClientStarter.Builder builder = new ClientStarter.Builder();
    ClientStarter starter = builder.setAppName("appName").setEtcdServer("http://1.8.8.4:2379,http://1.1.4.4:2379,http://1.1.1.1:2379").build();
    starter.startPipeline();
}
```
其中还可以setCaffeineSize(int size)设置本地缓存最大数量，默认5万，setPushPeriod(Long period)设置批量推送key的间隔时间，默认500ms，该值越小，上报热key越频繁，相应越及时，建议根据实际情况调整，如单机每秒qps10个，那么0.5秒上报一次即可，否则是空跑。该值最小为1，即1ms上报一次。

注意：

如果原有项目里使用了guava，需要升级guava为以下版本，否则过低的guava版本可能发生jar包冲突。或者删除自己项目里的guava的maven依赖，guava升级不会影响原有任何逻辑。

```
<dependency>
 <groupId>com.google.guava</groupId>
 <artifactId>guava</artifactId>
 <version>28.2-jre</version>
 <scope>compile</scope>
</dependency>
```

有时可能项目里没有直接依赖guava，但是引入的某个pom里引了guava，也需要将guava排除掉。



使用：

主要有如下4个方法可供使用

boolean JdHotKeyStore.isHotKey(String key)

Object JdHotKeyStore.get(String key)

void JdHotKeyStore.smartSet(String key, Object value) 

Object JdHotKeyStore.getValue(String key)



1 boolean isHotKey(String key) ，该方法会返回该key是否是热key，如果是返回true，如果不是返回false，并且会将key上报到探测集群进行数量计算。该方法通常用于判断只需要判断key是否热、不需要缓存value的场景，如刷子用户、接口访问频率等。

2 Object get(String key)，该方法返回该key本地缓存的value值，可用于判断是热key后，再去获取本地缓存的value值，通常用于redis热key缓存

3 void smartSet(String key, Object value)，方法给热key赋值value，如果是热key，该方法才会赋值，非热key，什么也不做

4 Object getValue(String key)，该方法是一个整合方法，相当于isHotKey和get两个方法的整合，该方法直接返回本地缓存的value。
如果是热key，则存在两种情况，1是返回value，2是返回null。返回null是因为尚未给它set真正的value，返回非null说明已经调用过set方法了，本地缓存value有值了。
如果不是热key，则返回null，并且将key上报到探测集群进行数量探测。


最佳实践：

1 判断用户是否是刷子

        if (JdHotKeyStore.isHotKey(“pin__” + thePin)) {
            //限流他，do your job
        } 
2 判断商品id是否是热点
     

           Object skuInfo = JdHotKeyStore.getValue("skuId__" + skuId);
           if(skuInfo == null) {
               JdHotKeyStore.smartSet("skuId__" + skuId, theSkuInfo);
           } else {
                  //使用缓存好的value即可
            }

   或者这样：

         

             if (JdHotKeyStore.isHotKey(key)) {
                  //注意是get，不是getValue。getValue会获取并上报，get是纯粹的本地获取
                  Object skuInfo = JdHotKeyStore.get("skuId__" + skuId);
                  if(skuInfo == null) {
                      JdHotKeyStore.smartSet("skuId__" + skuId, theSkuInfo);
                  } else {
                      //使用缓存好的value即可
                  }

             }



