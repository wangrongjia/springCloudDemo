## 配置中心git示例

随着线上项目变的日益庞大，每个项目都散落着各种配置文件，如果采用分布式的开发模式，需要的配置文件随着服务增加而不断增多。某一个基础服务信息变更，都会引起一系列的更新和重启，运维苦不堪言也容易出错。配置中心便是解决此类问题的灵丹妙药

Spring Cloud Config项目是一个解决分布式系统的配置管理方案。它包含了`Client`和`Server`两个部分：

`server`提供配置文件的存储、以接口的形式将配置文件的内容提供出去

`client`通过接口获取数据、并依据此数据初始化自己的应用。

Spring cloud使用git或svn存放配置文件，默认情况下使用git，我们先以git为例做一套示例。

### 1. 创建配置文件

在github上面创建了一个文件夹config-repo用来存放配置文件，为了模拟生产环境，我们创建以下三个配置文件：
```java
// 开发环境
codinger-config-dev.properties
// 测试环境
codinger-config-test.properties
// 生产环境
codinger-config-pro.properties
```

### 2. server端

#### 1. 添加依赖
```xml
	<!-- euraka client -->
       <dependency>
           <groupId>org.springframework.cloud</groupId>
           <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
       </dependency>
	<!-- config-server -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-config-server</artifactId>
		</dependency>
```

#### 2. 配置文件

`application.yml`
```yml
server:
  port: 8001
spring:
  application:
    name: spring-cloud-config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/wxpp/springCloudDemo/    # 配置git仓库的地址
          search-paths: config-repo                             # git仓库地址下的相对地址，可以配置多个，用,分割。
          username: wxpp                                        # git仓库的账号
          password: password                                    # git仓库的密码
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8000/eureka/   ## 注册中心eurka地址
```

#### 3. 启动类支持

`@EnableDiscoveryClient` eukeka client支持

`@EnableConfigServer` 配置中心支持

#### 4. 测试

访问 `http://localhost:8001/codinger-config/dev` 或者 `http://localhost:8001/codinger-config-dev.properties`

### 3. client

#### 1. 添加依赖

```xml
	<!-- eureka-client -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
	<!-- config -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-config</artifactId>
		</dependency>
	<!-- web -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
```

#### 2. 配置文件

需要两份 `application.properties` 和 `bootstrap.properties`

`application.properties`
```propertoes
spring.application.name=spring-cloud-config-client
server.port=8002
```

`bootstrap.properties`
```properties
spring.cloud.config.name=codinger-config
spring.cloud.config.profile=dev
spring.cloud.config.label=master
spring.cloud.config.discovery.enabled=true
spring.cloud.config.discovery.serviceId=spring-cloud-config-server

eureka.client.serviceUrl.defaultZone=http://localhost:8000/eureka/
```

`spring.cloud.config.discovery.enabled` ：开启Config服务发现支持

`spring.cloud.config.discovery.serviceId`：指定server端的name,也就是server端spring.application.name的值

`eureka.client.serviceUrl.defaultZone` ：指向配置中心的地址

#### 3. 启动类支持

`@EnableDiscoveryClient`

因为多个配置文件的原因，去配置中心查找配置的取值时，默认只用第一个application.properties查找，找不到就报错，以下是解决方法
```java
    /**
     * 在springboot的启动类中设置，一个配置文件中找不到继续找
     * @return
     */
	@Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
        c.setIgnoreUnresolvablePlaceholders(true);
        return c;
    }
```

#### 4. 测试

web接口
```java
@RestController
class HelloController {

    @Value("${codinger.hello}")
    private String hello;

    @RequestMapping("/hello")
    public String from() {
        return this.hello;
    }
}
```

访问 `http://localhost:8002/hello` 取到配置项的值






