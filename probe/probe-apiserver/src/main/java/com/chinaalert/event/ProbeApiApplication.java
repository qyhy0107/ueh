package com.chinaalert.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableAutoConfiguration
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,FreeMarkerAutoConfiguration.class},scanBasePackages ={"com.chinaalert.event"})
public class ProbeApiApplication{
	public static void main(String[] args){
		SpringApplication.run(ProbeApiApplication.class);
	}
}
