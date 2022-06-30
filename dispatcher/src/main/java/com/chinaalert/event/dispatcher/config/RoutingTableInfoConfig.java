package com.chinaalert.event.dispatcher.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chinaalert.event.dispatcher.domain.RoutingTable;
import com.chinaalert.event.dispatcher.domain.RoutingTableInfo;


/**
 * Dispatcher路由表信息,目前从配置文件中获取，后面需要修改为从数据库中获取
 * @author q
 *
 */
@Configuration
@ConfigurationProperties(prefix="handler")
public class RoutingTableInfoConfig {
	private List<RoutingTableInfo> data;

	@Bean(name="routingTable")
	public RoutingTable build() {
		Map<String,RoutingTableInfo> routingTable=new HashMap<String,RoutingTableInfo>();
		for(RoutingTableInfo info:data){
			routingTable.put(info.getType(), info);
		}
		return new RoutingTable(routingTable);
	}

	public List<RoutingTableInfo> getData() {
		return data;
	}

	public void setData(List<RoutingTableInfo> data) {
		this.data = data;
	}
}
