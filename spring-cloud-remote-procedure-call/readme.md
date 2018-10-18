## 微服务不同服务间的相互调用(feign)

### 1. eureka server

`spring-cloud-eureka-server`

略

### 2. 服务提供

`spring-cloud-producer`

启动类添加`@EnableDiscoveryClient`注解

`pom.xml`
```xml
	<!-- euraka client -->
       <dependency>
           <groupId>org.springframework.cloud</groupId>
           <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
       </dependency>
	<!-- web -->
	   <dependency>
	      <groupId>org.springframework.boot</groupId>
	      <artifactId>spring-boot-starter-web</artifactId>
   		</dependency>
```

`application.properties`
```properties
spring.application.name=spring-cloud-producer
server.port=9000
eureka.client.serviceUrl.defaultZone=http://localhost:8000/eureka/
```

一个controller
```java
@RestController
public class HelloController {
	
    @RequestMapping("/hello")
    public String index(@RequestParam String name) {
        return "hello "+name+"，this is first messge";
    }
}
```

### 3. 服务调用

`spring-cloud-consumer`

启动类添加`@EnableDiscoveryClient`  `@EnableFeignClients` 注解

`pom.xml`
```xml
	<!-- euraka client -->
       <dependency>
           <groupId>org.springframework.cloud</groupId>
           <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
       </dependency>
	<!-- feign -->
		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
     <!-- web -->
	   <dependency>
	      <groupId>org.springframework.boot</groupId>
	      <artifactId>spring-boot-starter-web</artifactId>
   		</dependency>
```

`配置文件`
```properties
spring.application.name=spring-cloud-consumer
server.port=9002
eureka.client.serviceUrl.defaultZone=http://localhost:8000/eureka/
```

`FeignClient`
```java
@FeignClient(name= "spring-cloud-producer")
public interface HelloRemote {

    @RequestMapping(value = "/hello")
    public String hello(@RequestParam(value = "name") String name);

}
```

controller
```java
@RestController
public class ConsumerController {

    @Autowired
    HelloRemote HelloRemote;
	
    @RequestMapping("/hello/{name}")
    public String index(@PathVariable("name") String name) {
        return HelloRemote.hello(name);
    }

}
```

访问 `http://localhost:8000`观察注册的两个服务

访问 `http://localhost:9000/hello?name=codinger`和`http://localhost:9002/hello/codinger`返回相同的结果，执行的都是producer的/hello








