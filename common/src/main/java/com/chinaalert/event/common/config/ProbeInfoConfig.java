package com.chinaalert.event.common.config;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chinaalert.event.common.domain.EventSourceType;
import com.chinaalert.event.common.domain.ProbeInfo;
import com.chinaalert.event.common.domain.ProbeType;


/**
 * 从配置读取Probe配置信息
 * 
 * @author hy
 */
@Configuration
public class ProbeInfoConfig {
	public final static String NEXTCHANNELNAME=UUID.randomUUID().toString();
	
	@Value("${jz.probe.threadCount}")
	private int threadCount;
	@Value("${jz.probe.name}")
	private String name;
	@Value("${jz.probe.type}")
	private ProbeType type;
	@Value("${jz.probe.version}")
	private String version;
	@Value("${jz.probe.key}")
	private String key;
	@Value("${jz.probe.port}")
	private int port;
	private int heartbeat=1000;
	private int timeout=3000;
	private String source="file";
	@Value("${jz.probe.eventSourceType}")
	private EventSourceType eventSourceType;
	@Bean
	public ProbeInfo buildProbeInfo() {
		ProbeInfo probe=new ProbeInfo();
		if(StringUtils.isEmpty(source)||source.equalsIgnoreCase("file")){
			probe.setName(this.name);
			probe.setType(this.type);
			probe.setVersion(this.version);
			probe.setKey(this.key);
			probe.setPort(this.port);
			probe.setHeartbeat(this.heartbeat);
			probe.setThreadCount(this.threadCount);
			probe.setTimeout(this.timeout);
			probe.setEventSourceType(eventSourceType);
		}else{
			//probe=getProbeInfo(this.probjectInfo.getId(),"");
		}
		
		return probe;
	}
}
