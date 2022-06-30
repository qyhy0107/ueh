package com.chinaalert.event.handler.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.domain.ProbeInfo;
import com.chinaalert.event.common.domain.ProjectInfo;
import com.chinaalert.event.common.utils.FastJsonUtils;
import com.chinaalert.event.handler.common.algorithm.EventCombine;
import com.chinaalert.event.handler.common.algorithm.EventCompress;
import com.chinaalert.event.handler.common.algorithm.EventDivide;
import com.chinaalert.event.handler.common.algorithm.EventFilter;
import com.chinaalert.event.handler.common.algorithm.EventManage;
import com.chinaalert.event.handler.common.algorithm.EventMapping;
import com.chinaalert.event.handler.common.algorithm.EventRecovery;
import com.chinaalert.event.handler.common.algorithm.EventSeverityUpOrDown;
import com.chinaalert.event.handler.common.config.EventHandlerRuleConfig;
import com.chinaalert.event.handler.common.config.EventIntegratedProbeConfig;
import com.chinaalert.event.handler.common.config.PlatformEventColumnConfig;
import com.chinaalert.event.handler.common.domain.EventHandlerRule;
import com.chinaalert.event.handler.common.domain.EventIntegratedProbe;
import com.chinaalert.event.handler.common.domain.PlatformProbeColumnMapping;
import com.chinaalert.event.handler.common.domain.PlatformProbeSeverityMapping;
import com.chinaalert.event.handler.common.filter.ProbeEventMessageHandler;
import com.chinaalert.event.handler.common.inner.EventMessageProducer;

import cn.mcinfotech.data.service.db.ColumnDefine;
import io.netty.channel.ChannelHandlerContext;

/**
 * 将从Dispatcher来的事件根据规则做事件处理,合并与过滤在其他地方实现
 * 1.接收校验，通过Probe(Probe Key)、Project(ID)进行校验,校验不通过的，废弃
 * 2.接收：校验通过的，进行事件规则处理：字段映射、级别映射
 * 3.压缩
 * 4.角色打标
 * 5.升降级
 * @author hy
 */
@Component
@Order(1)
public class DefaultHandlerFilter extends ProbeEventMessageHandler {
	private Logger logger = LogManager.getLogger(ProbeEventMessageHandler.class);
	@Resource
	private EventMessageProducer producer;
	@Resource
	DataSource dataSource;
	/*@Resource
	ProbeInfo probe2;*/
	@Resource
	PlatformEventColumnConfig ColumnDefineConfig;
	
	@Override
	public boolean beforeRuleHandler(ProjectInfo projectInfo, List<ProbeEventMessage> messages,ChannelHandlerContext ctx) {
		if(CollectionUtils.isEmpty(messages))return false;
		return true;
	}
	@Override
	public List<Map<String, Object>> mapping(List<ProbeEventMessage> messages, ProbeInfo probeInfo,ProjectInfo projectInfo, EventIntegratedProbeConfig eventIntegratedProbeConfig) {
		List<Map<String,Object>> handleredDatas=new ArrayList<Map<String,Object>>();
		for(ProbeEventMessage message:messages){
			//1.接收校验，通过Probe(Probe Key)、Project(ID)进行校验,校验不通过的，废弃
			//每个List应该是同一Probe、Project,尚未验证
			EventIntegratedProbe integratedProbe=this.eventIntegratedProbeConfig.getEventIntegratedProbe(projectInfo.getId(), message.getProbe().getKey());
			if(integratedProbe==null){
				logger.warn("invalid message , not found integrated probe , error message is "+message);
				continue;
			}
			if(integratedProbe.getColumnMapping()==null){
				logger.warn("invalid message , integrated probe column's mapping is not setup , error message is "+message);
				continue;
			}
			if(integratedProbe.getSeverityMapping()==null){
				logger.warn("invalid message , integrated probe severity's mapping is not setup , error message is "+message);
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("mapping columns "+integratedProbe.getColumnMapping());
				logger.debug("mapping severity :"+integratedProbe.getSeverityMapping());
			}
			PlatformProbeColumnMapping columnsSettings=FastJsonUtils.toBean(integratedProbe.getColumnMapping(), PlatformProbeColumnMapping.class);
			PlatformProbeSeverityMapping severitySettings=FastJsonUtils.toBean(integratedProbe.getSeverityMapping(), PlatformProbeSeverityMapping.class);
			Map<String,Object> eventMessage=(Map) FastJsonUtils.stringToCollect(message.getMessageBody());
			handleredDatas.add(EventMapping.excute(eventMessage,columnsSettings, severitySettings));
		}
		return handleredDatas;
	}
	@Override
	public List<ColumnDefine> getPlatformColumnDefine(long projectId) {
		Map<String,Object> conditions=new HashMap<String,Object>();
		conditions.put("isEnable", "Y");
		return ColumnDefineConfig.getPlatformEventColumn(projectInfo.getId(), conditions);
	}
	@Override
	public Collection<Map<String, Object>> compress(List<Map<String, Object>> mappingData,EventHandlerRuleConfig eventHandlerRulesConfig, ProbeInfo probeInfo, ProjectInfo projectInfo) {
		//拿优先级最高的一条压缩
		List<EventHandlerRule> compressRules = this.eventHandlerRulesConfig.getRules(projectInfo.getId(), probeInfo.getKey(), probeInfo.getEventSourceType(), "Z");
		List<EventHandlerRule> recoveryRules = this.eventHandlerRulesConfig.getRules(projectInfo.getId(), probeInfo.getKey(), probeInfo.getEventSourceType(), "RE");
		EventHandlerRule eventHandlerRule = null;
		if (CollectionUtils.isNotEmpty(recoveryRules)) eventHandlerRule=recoveryRules.get(0);
//		EventHandlerRule eventHandlerRule = null;
//		if (CollectionUtils.isNotEmpty(compressRules)) eventHandlerRule=compressRules.get(0);
//		EventHandlerRule compressRule=this.eventHandlerRulesConfig.getEventHandlerRule(projectInfo.getId(),probeInfo.getKey(), probeInfo.getEventSourceType(),"Z");
		return EventCompress.defaultExcute(mappingData, compressRules,dataSource,projectInfo.getId(),eventHandlerRule);
	}

	@Override
	protected Collection<Map<String, Object>> recovery(Collection<Map<String, Object>> compressedMessage, EventHandlerRuleConfig eventHandlerRulesConfig, ProbeInfo probeInfo, ProjectInfo projectInfo) {
		//拿优先级最高的一条恢复
		List<EventHandlerRule> recoveryRules = this.eventHandlerRulesConfig.getRules(projectInfo.getId(), probeInfo.getKey(), probeInfo.getEventSourceType(), "RE");
		EventHandlerRule eventHandlerRule = null;
		if (CollectionUtils.isNotEmpty(recoveryRules)) eventHandlerRule=recoveryRules.get(0);
//		EventHandlerRule compressRule=this.eventHandlerRulesConfig.getEventHandlerRule(projectInfo.getId(),probeInfo.getKey(), probeInfo.getEventSourceType(),"RE");
		return EventRecovery.excute(compressedMessage, eventHandlerRule,dataSource,projectInfo);
	}

	@Override
	public Collection<Map<String, Object>> divide(Collection<Map<String, Object>> rawData,List<ColumnDefine> ColumnDefines, EventHandlerRuleConfig eventHandlerRulesConfig,ProbeInfo probeInfo, ProjectInfo projectInfo) {
		List<EventHandlerRule> fillRules = this.eventHandlerRulesConfig.getEventHandlerRules(projectInfo.getId(), probeInfo.getKey(), null, "G");//事件分组不用eventSourceType区分，传null
		return EventDivide.excute(rawData,ColumnDefine.toMap(ColumnDefines),fillRules,dataSource);
	}
	@Override
	public Collection<Map<String, Object>> upOrDown(Collection<Map<String, Object>> rawData,List<ColumnDefine> ColumnDefines, EventHandlerRuleConfig eventHandlerRulesConfig,ProbeInfo probeInfo, ProjectInfo projectInfo) {
		//升级
//		EventHandlerRule upRule= this.eventHandlerRulesConfig.getEventHandlerRule(projectInfo.getId(),probeInfo.getKey(), probeInfo.getEventSourceType(),"U");
//		Collection<Map<String, Object>> upMessage=EventSeverityUpOrDown.excute(rawData,ColumnDefine.toMap(ColumnDefines),upRule);
		List<EventHandlerRule> upRules = this.eventHandlerRulesConfig.getRules(projectInfo.getId(), probeInfo.getKey(), probeInfo.getEventSourceType(), "U");
		Collection<Map<String, Object>> upMessage=EventSeverityUpOrDown.excute(rawData,ColumnDefine.toMap(ColumnDefines),upRules);
		//降级
//		EventHandlerRule downRule=this.eventHandlerRulesConfig.getEventHandlerRule(projectInfo.getId(),probeInfo.getKey(), probeInfo.getEventSourceType(),"D");
//		return EventSeverityUpOrDown.excute(upMessage,ColumnDefine.toMap(ColumnDefines),downRule);
		List<EventHandlerRule> downRules = this.eventHandlerRulesConfig.getRules(projectInfo.getId(), probeInfo.getKey(), probeInfo.getEventSourceType(), "D");
		return EventSeverityUpOrDown.excute(upMessage,ColumnDefine.toMap(ColumnDefines),downRules);
	}
	@Override
	public Collection<Map<String, Object>> filter(Collection<Map<String, Object>> rawData,List<ColumnDefine> ColumnDefines, EventHandlerRuleConfig eventHandlerRulesConfig,ProbeInfo probeInfo, ProjectInfo projectInfo) {
//		EventHandlerRule filterRule=this.eventHandlerRulesConfig.getEventHandlerRule(projectInfo.getId(),probeInfo.getKey(), probeInfo.getEventSourceType(),"F");
		List<EventHandlerRule> filterRules = this.eventHandlerRulesConfig.getRules(projectInfo.getId(), probeInfo.getKey(), probeInfo.getEventSourceType(), "F");
		return EventFilter.excute(rawData,ColumnDefine.toMap(ColumnDefines),filterRules);
	}

	@Override
	public Collection<Map<String, Object>> combine(Collection<Map<String, Object>> rawData, List<ColumnDefine> ColumnDefines, EventHandlerRuleConfig eventHandlerRulesConfig, ProbeInfo probeInfo, ProjectInfo projectInfo) {
		List<EventHandlerRule> combineRules = this.eventHandlerRulesConfig.getEventHandlerRules(projectInfo.getId(), probeInfo.getKey(), null, "C");//事件合并不用eventSourceType区分，传null

		return EventCombine.excute(rawData,ColumnDefine.toMap(ColumnDefines),combineRules);
	}

	@Override
	public Collection<Map<String, Object>> doIt(Collection<Map<String, Object>> rawData,
			List<ColumnDefine> ColumnDefines, EventHandlerRuleConfig eventHandlerRulesConfig, ProbeInfo probeInfo, ProjectInfo projectInfo) {
		return rawData;
	}
	@Override
	public void afterRuleHandler(ProjectInfo projectInfo,ProbeInfo probeInfo,Collection<Map<String, Object>> rawMessages) {
		//8.入库,并通知转发
		//8.1入库
		//PlatformEventDBUtils.insert(dataSource,projectInfo.getId(),rawData);
		//Map<String,Object> conditions=new HashMap<String,Object>();
		//conditions.put("isEnable", "Y");
		//conditions.put("columnTypeModify", "Y");
		//List<ColumnDefine> columnDefines=ColumnDefineConfig.getPlatformEventColumn(10, conditions);
		if (CollectionUtils.isEmpty(rawMessages)) return;
		//8.1.1筛选可恢复事件
		Iterator<Map<String, Object>> iterator = rawMessages.iterator();
		List<Map<String, Object>> reMessages = new ArrayList<>();//恢复事件
		List<Map<String, Object>> notReMessages = new ArrayList<>();//其他事件
		while (iterator.hasNext()) {
			Map<String, Object> rawMessage = iterator.next();
			String eventType = String.valueOf(rawMessage.get("EventType"));
			String eventSeverityType = String.valueOf(rawMessage.get("EventSeverityType"));
			//可恢复事件:EventType(P不可恢复字段，S可恢复事件) EventSeverityType(1告警事件，2恢复事件，EventType=S时有效)
			if ("S".equalsIgnoreCase(eventType)&&"2".equals(eventSeverityType)){
				reMessages.add(rawMessage);
			}else {
				notReMessages.add(rawMessage);
			}
		}
		//8.1.2入库
		if (reMessages.size()>0) EventManage.createRecoveryEvent(dataSource, projectInfo.getId(), reMessages);
		//处理完毕的事件同时入实时表和历史表
		if (notReMessages.size()>0) EventManage.createEvent(dataSource, projectInfo.getId(), notReMessages);
		//8.2通知
		Map<String,String> excludes=new HashMap<String,String>();
		excludes.put("FilterFlag", "NF#NN#NS");
		producer.push(ProbeEventMessage.collectionToProbeEventMessage(projectInfo,probeInfo,rawMessages,excludes));
	}
}
