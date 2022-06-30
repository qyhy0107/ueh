package com.chinaalert.event.dispatcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chinaalert.event.dispatcher.domain.DispatcherInfo;


/**
 * Dispatcher的dispatcher配置
 * 
 * @author hy
 */
@Configuration
public class DispatcherInfoConfig {
	@Value("${dispatcher.batch}")
	private int batch;
	@Value("${dispatcher.interval}")
	private int interval;
	/*@Value("${dispatcher.timerEnable}")
	private boolean timerEnable;*/
	@Bean
	public DispatcherInfo buildDispatcherInfo() {
		DispatcherInfo probe=new DispatcherInfo();
		probe.setBatch(batch);
		probe.setInterval(interval);
		//probe.setTimerEnable(timerEnable);
		return probe;
	}
}
