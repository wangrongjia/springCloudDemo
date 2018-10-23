## 服务网关zuul
前面的文章我们介绍了，Eureka用于服务的注册于发现，Feign支持服务的调用以及均衡负载，Hystrix处理服务的熔断防止故障扩散，Spring Cloud Config服务集群配置中心，似乎一个微服务框架已经完成了。

我们还是少考虑了一个问题，外部的应用如何来访问内部各种各样的微服务呢？在微服务架构中，后端服务往往不直接开放给调用端，而是通过一个API网关根据请求的url，路由到相应的服务。当添加API网关后，在第三方调用端和服务提供方之间就创建了一面墙，这面墙直接与调用方通信进行权限控制，后将请求均衡分发给后台服务端。

### 1. eureka server

### 2. producer 服务

同spring-cloud-remote-procedure-call中的producer服务

### 3. zuul

#### 1. 添加依赖

```xml
	<!-- eureka-client -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
	<!-- zuul -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-zuul</artifactId>
		</dependency>
```

#### 2.配置文件
```properties
spring.application.name=spring-cloud-zuul
server.port=8888

eureka.client.serviceUrl.defaultZone=http://localhost:8000/eureka/
```

#### 3. 启动类添加注解

`@EnableZuulProxy`

默认情况下，Zuul会代理所有注册到Eureka Server的微服务，并且Zuul的路由规则如下：`http://ZUUL_HOST:ZUUL_PORT/微服务在Eureka上的serviceId/**`

访问 `http://localhost:8888/spring-cloud-producer/hello?name=codinger`

也可以显示的配置

比如 配置文件中加上

```properties
zuul.routes.producer.path=/producer/**
zuul.routes.producer.serviceId=spring-cloud-producer
```

也可以访问 `http://localhost:8888/producer/hello?name=codinger`








