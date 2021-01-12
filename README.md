## SummerCloud

分布式微服务系统架构

本项目参考个人学习仓库: https://github.com/WswSummer15/myspringcloud2020

## Java版本

```text
Java1.8
```

## 框架版本

```text
<spring.boot.version>2.2.2.RELEASE</spring.boot.version>
<spring.cloud.version>Hoxton.SR1</spring.cloud.version>
<spring.cloud.alibaba.version>2.1.0.RELEASE</spring.cloud.alibaba.version>
```

## SpringCloud组件选择

```text
1. 注册中心 Nacos
2. 配置中心 Nacos
3. 服务调用 OpenFeign
4. 负载均衡 Ribbon
5. 服务限流降级 Sentinel
6. 网关 Gateway
7. 服务总线 SpringCloud Bus
8. 缓存中间件 Redis
9. 消息中间件 RabbitMQ
```

## 项目结构

```text
summercloud-gateway-service -> 4000
summercloud-auth-service -> 3000
summercloud-main-service -> 4001
summercloud-task-service -> 4002
```

## 程序运行

### 本地环境

```text
最新版本即可
1. Nacos
cd bin -> .\startup.cmd

2. Sentinel(jar包)
自定义端口启动 -> java -jar .\sentinel-dashboard-1.8.0.jar --server.port=9090

3. RabbitMQ
省略
4. Redis
省略
```

### 使用Nacos作为配置中心管理配置文件

配置文件格式: {微服务名称}-{环境}.{文件格式} 例如: summercloud-main-service-dev.yaml

#### 1. 存储公共配置的配置文件summercloud-dev.yaml

```yaml
jwt:
  secretKey: 1234567890-1234567890-1234567890
```

#### 2. 配置文件summercloud-main-service-dev.yaml

```yaml
server:
  port: 4001

spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:mysql://39.107.80.231:3306/task-system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: HengTian
    password: ***

  redis:
    host: 39.107.80.231
    port: 6379
    lettuce:
      pool:
        # 连接池最大连接数(使用负值表示没有限制) 默认为8
        max-active: 8
        # 连接池最大阻塞等待时间(使用负值表示没有限制) 默认为-1
        max-wait: -1ms
        # 连接池中的最大空闲连接 默认为8
        max-idle: 8
        # 连接池中的最小空闲连接 默认为 0
        min-idle: 0

  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
    publisher-confirms: true
    publisher-returns: true
    template:
      # 只要消息抵达队列,以异步方式优先回调returnsConfirm
      mandatory: true
    listener:
      simple:
        # 消费端手动ack消息
        acknowledge-mode: manual

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.wsw.summercloud.domain

management:
  endpoints:
    web:
      exposure:
        include: '*' 
```

#### 3. 配置文件summercloud-auth-service-dev.yaml

```yaml
server:
  port: 4050

spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:mysql://39.107.80.231:3306/task-system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: HengTian
    password: HengTian0.0

  redis:
    host: 39.107.80.231
    port: 6379
    lettuce:
      pool:
        # 连接池最大连接数(使用负值表示没有限制) 默认为8
        max-active: 8
        # 连接池最大阻塞等待时间(使用负值表示没有限制) 默认为-1
        max-wait: -1ms
        # 连接池中的最大空闲连接 默认为8
        max-idle: 8
        # 连接池中的最小空闲连接 默认为 0
        min-idle: 0

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.wsw.summercloud.domain

management:
  endpoints:
    web:
      exposure:
        include: '*'
```

#### 4. 配置文件summercloud-task-service-dev.yaml

```yaml
server:
  port: 4002

spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:mysql://39.107.80.231:3306/task-system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: HengTian
    password: ***

  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
    publisher-confirms: true
    publisher-returns: true
    template:
      # 只要消息抵达队列,以异步方式优先回调returnsConfirm
      mandatory: true
    listener:
      simple:
        # 消费端手动ack消息
        acknowledge-mode: manual

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.wsw.summercloud.domain

management:
  endpoints:
    web:
      exposure:
        include: '*'
```

配置文件在Nacos配置中心配置好后即可依次启动服务。

### 访问服务

通过gateway网关暴露服务,通过网关访问具体服务即可,比如访问summercloud-gateway-service微服务:

```text
http://localhost:4000/summercloud-gateway-service/task/***
```