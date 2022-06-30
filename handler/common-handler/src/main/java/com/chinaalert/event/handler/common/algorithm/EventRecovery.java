package com.chinaalert.event.handler.common.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.util.CollectionUtils;

import com.chinaalert.event.common.domain.ProjectInfo;
import com.chinaalert.event.common.utils.FastJsonUtils;
import com.chinaalert.event.handler.common.domain.EventHandlerRule;
import com.chinaalert.event.handler.common.domain.EventHandlerRuleExpression;

import cn.mcinfotech.data.service.domain.DataLoadParams;
import cn.mcinfotech.data.service.domain.SQLEngine;
import cn.mcinfotech.data.service.util.DataServiceUtils;

/**
 * 事件恢复
 *
 */
public class EventRecovery {
	/**
	 * @param rawMessages 原始消息
	 * @param rule 恢复规则
	 * @param dataSource 数据源
	 * @param projectInfo 项目信息
	 * @return 过滤过的事件
	 */
	public static Collection<Map<String, Object>> excute(Collection<Map<String, Object>> rawMessages, EventHandlerRule rule, DataSource dataSource, ProjectInfo projectInfo){
		//todo 恢复动作一个线程   后续处理一个线程
		if(CollectionUtils.isEmpty(rawMessages))return null;
		if(rule==null)return rawMessages;

		//1.筛选可恢复事件
		Iterator<Map<String, Object>> iterator = rawMessages.iterator();
		List<Map<String, Object>> reMessages = new ArrayList<>();
		while (iterator.hasNext()) {
			Map<String, Object> rawMessage = iterator.next();
			String eventType = String.valueOf(rawMessage.get("EventType"));
			String eventSeverityType = String.valueOf(rawMessage.get("EventSeverityType"));
			//可恢复事件:EventType(P不可恢复字段，S可恢复事件) EventSeverityType(1告警事件，2恢复事件，EventType=S时有效)
			if ("S".equalsIgnoreCase(eventType)&&"2".equals(eventSeverityType)){
				rawMessage.put("recoveredStatus", 2);//状态标记为恢复
				reMessages.add(rawMessage);
				//iterator.remove();
			}
		}

		//2.恢复条件重新封装到list
		String expressionStr = rule.getExpression();
		List<EventHandlerRuleExpression> expressions= FastJsonUtils.toList(expressionStr, EventHandlerRuleExpression.class);

		//3.逐条更新事件信息
		if (reMessages.size()>0) {//可恢复事件：实时表删除已恢复事件、历史表更新恢复事件状态
			for (Map<String, Object> reVoMessage : reMessages) {
				//3.1 前端传过来的动态条件 where
				Map<String, Object> filter = new HashMap<>();
				ArrayList<Map<String,Object>> reDtoMessages = new ArrayList<>();
				for (EventHandlerRuleExpression expression : expressions) {
					String conditionColumn = expression.getConditionColumn();
					HashMap<String, Object> reDtoMessage = new HashMap<>();
					reDtoMessage.put("reColumn",conditionColumn);
					reDtoMessage.put("reValue",reVoMessage.get(conditionColumn));
					reDtoMessages.add(reDtoMessage);
				}
				filter.put("conditions",reDtoMessages);
				//3.2 固定更新字段  update
				//filter.put("severity", String.valueOf(reVoMessage.get("Severity")));
				//filter.put("oldSeverity", String.valueOf(reVoMessage.get("OldSeverity")));
				filter.put("lastOccurrence", String.valueOf(reVoMessage.get("LastOccurrence")));
				filter.put("deleteTime", String.valueOf(reVoMessage.get("LastOccurrence")));
				filter.put("recoveredEeventID", String.valueOf(reVoMessage.get("EventID")));
				filter.put("recoveredStatus", 2);
				DataLoadParams params = new DataLoadParams();
				params.setDcName("operateEvent");
				params.setProjectId(projectInfo.getId());
				params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
				params.setStart(1);
				params.setLimit(-10);
				params.setEngine(SQLEngine.Freemarker);
				DataServiceUtils.dataLoad(dataSource, params);
			}
		}

		return rawMessages;
	}
}
