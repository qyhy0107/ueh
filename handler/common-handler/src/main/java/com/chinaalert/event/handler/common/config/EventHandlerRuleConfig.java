package com.chinaalert.event.handler.common.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.chinaalert.event.common.domain.EventSourceType;
import com.chinaalert.event.common.utils.FastJsonUtils;
import com.chinaalert.event.handler.common.domain.EventHandlerRule;

import cn.mcinfotech.data.service.domain.DataLoadParams;
import cn.mcinfotech.data.service.domain.ResultPattern;
import cn.mcinfotech.data.service.domain.SQLEngine;
import cn.mcinfotech.data.service.util.DataServiceUtils;

@Component
public class EventHandlerRuleConfig {
	@Autowired
	DataSource dataSource;

	/**
	 * 根据ProbeKey（EventSource)查询事件处理规则
	 * @param projectId
	 * @param eventSource 传入ProbeKey
	 * @param ruleType 压缩规则，C:combine,R:rich,Z:compress,U：升级，D:降级，F:过滤
	 * @return
	 */
	public EventHandlerRule getEventHandlerRule(long projectId,String eventSource,EventSourceType eventSourceType,String ruleType){
		EventHandlerRule column=null;
		Map<String,Object> filter=new HashMap<String,Object>();
		//if(StringUtils.isNotEmpty(eventSource)){
		filter.put("key", eventSource);
		//}
		filter.put("eventSourceType", eventSourceType);
		filter.put("ruleType", ruleType);
		filter.put("isEnable", "Y");
		DataLoadParams params=new DataLoadParams();
		params.setEngine(SQLEngine.Freemarker);
		params.setDcName("eventRulesSelectOne");
		params.setProjectId(projectId);
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		params.setStart(1);
		params.setLimit(-10);
		ResultPattern result=DataServiceUtils.dataLoad(dataSource, params);
		if(result.isSuccess()&&!result.isEmpty()){
			column=(EventHandlerRule) FastJsonUtils.convertJSONToObject(FastJsonUtils.convertObjectToJSON(result.getMapData()),EventHandlerRule.class);
		}
		return column;
	}
	public List<EventHandlerRule> getEventHandlerRules(long projectId,Map<String,Object> condition){
		List<EventHandlerRule> columns=null;
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("projectId", projectId);
		filter.putAll(condition);
		DataLoadParams params=new DataLoadParams();
		params.setEngine(SQLEngine.Freemarker);
		params.setDcName("eventRulesSelect");
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		params.setStart(1);
		params.setLimit(-10);
		ResultPattern result=DataServiceUtils.dataLoad(dataSource, params);
		if(result.isSuccess()&&!result.isEmpty()){
			columns=(List<EventHandlerRule>) FastJsonUtils.convertJSONToObject(FastJsonUtils.convertObjectToJSON(result.getDatas()),EventHandlerRule.class);
		}
		return columns;
	}

	/**
	 * 根据事件源key和规则类型获取规则列表
	 * @param projectId 项目id
	 * @param eventSource probe key
	 * @param eventSourceType 事件源
	 * @param ruleType 规则类型
	 * @return
	 */
	public List<EventHandlerRule> getEventHandlerRules(long projectId,String eventSource,EventSourceType eventSourceType,String ruleType){
		List<EventHandlerRule> columns=null;
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("eventSourceType", eventSourceType);
		filter.put("ruleType", ruleType);
		filter.put("eventSource", eventSource);
		filter.put("isEnable", "Y");
		DataLoadParams params=new DataLoadParams();
		params.setDcName("findEventRoles");
		params.setProjectId(projectId);
		params.setEngine(SQLEngine.Freemarker);
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		params.setStart(1);
		params.setLimit(-10);
		ResultPattern result=DataServiceUtils.dataLoad(dataSource, params);
		if(result.isSuccess()&&!result.isEmpty()){
			columns= JSON.parseArray(JSON.toJSONString(result.getDatas()),EventHandlerRule.class);
		}
		return columns;
	}
	/**
	 * 根据事件源分类key或事件源获取处理规则
	 * @param projectId 项目id
	 * @param eventSource probe key
	 * @param eventSourceType 事件源
	 * @param ruleType 规则类型
	 * @return
	 */
	public List<EventHandlerRule> getRules(long projectId,String eventSource,EventSourceType eventSourceType,String ruleType){
		List<EventHandlerRule> columns=null;
		Map<String,Object> filter=new HashMap<>();
		filter.put("eventSourceType", eventSourceType);
		filter.put("ruleType", ruleType);
		filter.put("eventSource", eventSource);
		filter.put("isEnable", "Y");
		DataLoadParams params=new DataLoadParams();
		params.setDcName("findEventRules");
		params.setProjectId(projectId);
		params.setEngine(SQLEngine.Freemarker);
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		params.setStart(1);
		params.setLimit(-10);
		ResultPattern result=DataServiceUtils.dataLoad(dataSource, params);
		if(result.isSuccess()&&!result.isEmpty()){
			columns= JSON.parseArray(JSON.toJSONString(result.getDatas()),EventHandlerRule.class);
		}
		return columns;
	}
}
