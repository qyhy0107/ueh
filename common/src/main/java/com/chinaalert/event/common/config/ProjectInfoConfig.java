package com.chinaalert.event.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chinaalert.event.common.domain.ProjectInfo;


/**
 * 从配置读取Project信息
 * @author q
 *
 */
@Configuration
public class ProjectInfoConfig {
	
	@Value("${project.id}")
	private int id;
	@Value("${project.name}")
	private String name;	
	private String source="file";
	
	@Bean
	public ProjectInfo buildProjectInfo() {
		ProjectInfo info=new ProjectInfo();
		info.setId(id);
		info.setName(name);
		info.setSource(source);
		return info;
	}
}
