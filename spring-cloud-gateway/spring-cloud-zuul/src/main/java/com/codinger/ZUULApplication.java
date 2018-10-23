package com.codinger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class ZUULApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZUULApplication.class, args);
	}
}
