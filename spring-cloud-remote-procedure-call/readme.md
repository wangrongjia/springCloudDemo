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

## 服务调用的熔断机制(Hystrix)

在微服务架构中通常会有多个服务层调用，基础服务的故障可能会导致级联故障，进而造成整个系统不可用的情况，这种现象被称为服务`雪崩效应`

### Hystrix特性

`1. 断路器机制`

断路器很好理解, 当Hystrix Command请求后端服务失败数量超过一定比例(默认50%), 断路器会切换到开路状态(Open). 这时所有请求会直接失败而不会发送到后端服务. 断路器保持在开路状态一段时间后(默认5秒), 自动切换到半开路状态(HALF-OPEN). 这时会判断下一次请求的返回情况, 如果请求成功, 断路器切回闭路状态(CLOSED), 否则重新切换到开路状态(OPEN). Hystrix的断路器就像我们家庭电路中的保险丝, 一旦后端服务不可用, 断路器会直接切断请求链, 避免发送大量无效请求影响系统吞吐量, 并且断路器有自我检测并恢复的能力.

`2. Fallback`

Fallback相当于是降级操作. 对于查询操作, 我们可以实现一个fallback方法, 当请求后端服务出现异常的时候, 可以使用fallback方法返回的值. fallback方法的返回值一般是设置的默认值或者来自缓存.

`3. 资源隔离`

在Hystrix中, 主要通过线程池来实现资源隔离. 通常在使用的时候我们会根据调用的远程服务划分出多个线程池. 例如调用产品服务的Command放入A线程池, 调用账户服务的Command放入B线程池. 这样做的主要优点是运行环境被隔离开了. 这样就算调用服务的代码存在bug或者由于其他原因导致自己所在线程池被耗尽时, 不会对系统的其他服务造成影响. 但是带来的代价就是维护多个线程池会对系统带来额外的性能开销. 如果是对性能有严格要求而且确信自己调用服务的客户端代码不会出问题的话, 可以使用Hystrix的信号模式(Semaphores)来隔离资源.

### Feign Hystrix

因为熔断只是作用在服务调用这一端，因此我们根据上一篇的示例代码只需要改动spring-cloud-consumer项目相关代码就可以。因为，Feign中已经依赖了Hystrix所以在maven配置上不用做任何改动。

#### 1.配置文件
```properties
feign.hystrix.enabled=true
```

#### 2、创建回调类
```java
@Component
public class HelloRemoteHystrix implements HelloRemote{

    @Override
    public String hello(@RequestParam(value = "name") String name) {
        return "hello" +name+", this messge send failed ";
    }
}
```

#### 3、FeignClient添加fallback属性
```java
@FeignClient(name= "spring-cloud-producer",fallback = HelloRemoteHystrix.class)
public interface HelloRemote {

    @RequestMapping(value = "/hello")
    public String hello(@RequestParam(value = "name") String name);

}
```

>这样每个service都要写一个fallback实现类，而且还要重写每个方法，能不能统一处理？写一个实现类一个方法搞定？   
>**并不是每一个都需要写fallback,没有fallback也会熔断，熔断机制的目的是降低一个服务崩溃后对其他服务的影响**，也就是说一个服务大量调用另一个服务提供的某个接口时，如果被调用的服务崩了，而调用者没有加熔断，自己的服务也会因为频繁的请求崩溃服务而崩溃(有可能)，加上熔断机制，则不会。







