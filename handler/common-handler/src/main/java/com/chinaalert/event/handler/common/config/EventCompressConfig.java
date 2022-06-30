package com.chinaalert.event.handler.common.config;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import com.chinaalert.event.common.utils.FastJsonUtils;

import cn.mcinfotech.data.service.domain.DataLoadParams;
import cn.mcinfotech.data.service.domain.ResultPattern;
import cn.mcinfotech.data.service.util.DataServiceUtils;
@Component
public class EventCompressConfig {

	public static List<Map<String, Object>> getAlarmEventByCondition(Map<String, Object> message, List<String> conditionColumns, String cycleTime, DataSource dataSource, long projectID) {
		LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(Long.valueOf(cycleTime));
		long l = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
		Map<Object, Object> filter = new HashMap<>();
		for (String columnName : conditionColumns) {
			Object columnValue = message.get(columnName);
			filter.put(columnName, columnValue);
		}
		if ("S".equalsIgnoreCase(String.valueOf(message.get("EventType")))) {
			filter.put("EventSeverityType",message.get("EventSeverityType"));
		}
		filter.put("cycleTime", l);
		DataLoadParams params=new DataLoadParams();
//		params.setProjectId(Long.valueOf((String) message.get("projectId")));
		params.setProjectId(projectID);
		params.setDcName("getAlarmEventByCondition");
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		ResultPattern resultPattern = DataServiceUtils.dataLoad(dataSource, params);

		return resultPattern.getDatas();
	}

	public static void main(String[] args) {
		LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(Long.valueOf("30"));
		long l = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
		System.out.println(l);
	}

	public static void updateAlarmEventByCondition(Map<Object, Object> filter, DataSource dataSource, long projectID) {
		DataLoadParams params=new DataLoadParams();
		params.setProjectId(projectID);
		params.setDcName("updateAlarmEventByCondition");
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		DataServiceUtils.dataLoad(dataSource, params);
	}
}
