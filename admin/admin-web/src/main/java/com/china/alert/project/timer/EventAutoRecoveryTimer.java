package com.china.alert.project.timer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import com.china.alert.project.system.domain.SysDictData;
import com.china.alert.project.system.service.ISysDictDataService;
import com.chinaalert.event.common.utils.FastJsonUtils;

import cn.mcinfotech.data.service.domain.DataLoadParams;
import cn.mcinfotech.data.service.domain.ResultPattern;
import cn.mcinfotech.data.service.util.DataServiceUtils;

/**
 * 20210827,hy,需要添加定时开关功能，可以在配置文件中设置
 *
 * @author q
 */
@Configuration
@EnableScheduling
public class EventAutoRecoveryTimer implements SchedulingConfigurer {
	private String dictType = "event_setting";
	private String dictLabel = "auto_recovery_interval";
	private String dictLabelCycle = "auto_recovery_cycle";
	private long projectId=10l;
	@Autowired
	DataSource dataSource;
	@Autowired
	ISysDictDataService dictDataService;
	
	
	/*
	 * @Autowired DictDataConfig dictData;
	 */
	/*
	 * @Autowired ProjectInfo project;
	 */
	/*
	 * @Autowired DispatcherInfo dispatcherInfo;
	 */

	/**
	 * 执行定时任务.
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(() -> {
			// 1.添加任务内容(Runnable)
			// 自动恢复不可恢复事件
			//Map<String, String> recoverySettings = dictData.getDictData(project.getId(), dictType, dictLabelCycle);
			SysDictData recoveryTime=this.dictDataService.selectDictDataByTypeAndLabel(10, dictType, dictLabelCycle);
			//String recoveryTime = recoverySettings.get("dict_value");
			if (StringUtils.isBlank(recoveryTime.getDictValue())) {
				recoveryTime.setDictValue("8");
			}
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put("dictType", dictType);
			filter.put("dictLabel", dictLabel);
			filter.put("recoveryTime", recoveryTime.getDictValue());
			DataLoadParams params = new DataLoadParams();
			params.setProjectId(10l);
			params.setDcName("autoCloseEvents");
			params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
			params.setStart(1);
			params.setLimit(0);
			DataServiceUtils.dataLoad(dataSource, params);
			// 2.自动确认已恢复事件
			// 2.1查询已恢复未确认事件ID 2小时
			DataLoadParams queryParams = new DataLoadParams();
			queryParams.setProjectId(projectId);
			queryParams.setDcName("queryEventID");
			queryParams.setFilter(FastJsonUtils.convertObjectToJSON(new HashMap<String, Object>()));
			ResultPattern queryResult = DataServiceUtils.dataLoad(dataSource, queryParams);
			// 2.2根据恢复事件ID自动确认事件并记录日志 2小时
			if (queryResult.isSuccess() && queryResult.getListData().size() > 0) {
				for (Object eventID : queryResult.getListData()) {
					Map<String, Object> acknowledgeFilter = new HashMap<String, Object>();
					acknowledgeFilter.put("EventID", eventID);
					DataLoadParams acknowledgeParams = new DataLoadParams();
					acknowledgeParams.setProjectId(projectId);
					acknowledgeParams.setDcName("acknowledgeEvents");
					acknowledgeParams.setFilter(FastJsonUtils.convertObjectToJSON(acknowledgeFilter));
					DataServiceUtils.dataLoad(dataSource, acknowledgeParams);
				}
			}
			// 3.通知类告警24小时后自动恢复
			ResultPattern notificationAlarmRes = getEventIdBySecurity();
			List<Object> eventIds = notificationAlarmRes.getListData();
			if (notificationAlarmRes.isSuccess() && CollectionUtils.isNotEmpty(eventIds)) {
				ResultPattern deleteAlarmRes = deleteAlarmFromStatus(eventIds);
				if (deleteAlarmRes.isSuccess()) {
					updateAlarmFromHistory(eventIds);
				}
			}

		}, triggerContext -> {
			// 2.设置执行周期(Trigger)
			// 2.1 从数据库获取执行周期
			//Map<String, String> intervalSettings = dictData.getDictData(project.getId(), dictType, dictLabel);
			SysDictData interval=this.dictDataService.selectDictDataByTypeAndLabel(this.projectId, dictType, dictLabel);
			String cronExpression = null;
			// 2.2 合法性校验.
			if (interval == null || StringUtils.isEmpty(interval.getDictValue())) {
				cronExpression = "0 0 0/4 * * * ";
			} else {
				cronExpression = interval.getDictValue();
			}
			// 2.3 返回执行周期(Date)
			return new CronTrigger(cronExpression).nextExecutionTime(triggerContext);
		});
	}

	private ResultPattern updateAlarmFromHistory(List<Object> eventIds) {
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("ids", eventIds);
		DataLoadParams queryParams = new DataLoadParams();
		queryParams.setProjectId(this.projectId);
		queryParams.setDcName("updateAlarmFromHistory");
		queryParams.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		return DataServiceUtils.dataLoad(dataSource, queryParams);
	}

	private ResultPattern deleteAlarmFromStatus(List<Object> eventIds) {
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("ids", eventIds);
		DataLoadParams queryParams = new DataLoadParams();
		queryParams.setProjectId(this.projectId);
		queryParams.setDcName("deleteAlarmFromStatus");
		queryParams.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		return DataServiceUtils.dataLoad(dataSource, queryParams);
	}

	private ResultPattern getEventIdBySecurity() {
		LocalDateTime now = LocalDateTime.now();
		long milliSecond = now.minusDays(1).toInstant(ZoneOffset.of("+8")).toEpochMilli();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("FirstOccurrence", milliSecond);
		DataLoadParams queryParams = new DataLoadParams();
		queryParams.setProjectId(this.projectId);
		queryParams.setDcName("getEventIdBySecurity");
		queryParams.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		return DataServiceUtils.dataLoad(dataSource, queryParams);
	}
}
