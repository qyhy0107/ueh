package com.chinaalert.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(exclude = {FreeMarkerAutoConfiguration.class})
public class TransmitApplication /*extends SpringBootServletInitializer*/ {
	public static void main(String[] args){
		SpringApplication.run(TransmitApplication.class);
	}
	/**
	 * 打包为war时需要实现该方法
	 */
	/*@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(TransmitApplication.class);
	}*/
}
