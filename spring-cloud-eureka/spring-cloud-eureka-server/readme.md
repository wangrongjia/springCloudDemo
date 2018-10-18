Eureka是Netflix开源的一款提供服务注册和发现的产品，它提供了完整的Service Registry和Service Discovery实现。也是springcloud体系中最重要最核心的组件之一

[SPRING INITIALIZR](https://start.spring.io/)在线生成  添加Eureka Server的依赖

![eureka-architecture-overview](src/main/resources/static/images/eureka-architecture-overview.png "eureka-architecture-overview")

上图简要描述了Eureka的基本架构，由3个角色组成：

1、`Eureka Server`

提供服务注册和发现  

2、`Service Provider`

服务提供方
将自身服务注册到Eureka，从而使服务消费方能够找到  

3、`Service Consumer`

服务消费方
从Eureka获取注册服务列表，从而能够消费服务  

启动类添加`@EnableEurekaServer`的注解

application.properties
```properties
server.port=8000
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true

eureka.client.serviceUrl.defaultZone=http://localhost:8000/eureka/
```

**eureka.client.register-with-eureka** ：表示是否将自己注册到Eureka Server，默认为true。  
**eureka.client.fetch-registry** ：表示是否从Eureka Server获取注册信息，默认为true。  
**eureka.client.serviceUrl.defaultZone** ：设置与Eureka Server交互的地址，查询服务和注册服务都需要依赖这个地址。默认是http://localhost:8761/eureka ；多个地址可使用 , 分隔。  
