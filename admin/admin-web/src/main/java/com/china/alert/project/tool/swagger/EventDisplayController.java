package com.china.alert.project.tool.swagger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.china.alert.framework.aspectj.lang.annotation.Log;
import com.china.alert.framework.aspectj.lang.enums.BusinessType;
import com.china.alert.framework.web.controller.BaseController;
import com.china.alert.project.system.domain.SysDictData;
import com.china.alert.project.system.service.ISysDictDataService;
import com.chinaalert.event.common.dispatcher.DispatcherHolder;
import com.chinaalert.event.common.domain.EventSourceType;
import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.domain.ProbeInfo;
import com.chinaalert.event.common.domain.ProbeType;
import com.chinaalert.event.common.domain.ProjectInfo;
import com.chinaalert.event.common.utils.FastJsonUtils;

import cn.mcinfotech.data.service.domain.DataLoadParams;
import cn.mcinfotech.data.service.domain.ResultPattern;
import cn.mcinfotech.data.service.exception.DataServiceException;
import cn.mcinfotech.data.service.util.DataServiceUtils;

/**
 * @author: hy
 * @description: 事件管理
 */
@RestController
@RequestMapping("/event")
public class EventDisplayController extends BaseController {
	private String nettyClientId = EventDisplayController.class.getName();
	@Autowired
	private DataSource dataSource;
	@Autowired
	private ISysDictDataService dictDataService;

	/**
	 * 接收从事件管理来的事件，直接发送到通知模块
	 * 
	 * @param params
	 * @return
	 */
	@Log(businessType = BusinessType.TX)
	@PostMapping(value = "/tx/receive")
	public ResultPattern notify(@RequestBody String params) {
		ResultPattern resultPattern = new ResultPattern();
		DataLoadParams loadParams = new DataLoadParams();
		try {
			loadParams = JSON.parseObject(params, DataLoadParams.class);
			if (loadParams.getProjectId() == null || loadParams.getProjectId() < 1) {
				throw new DataServiceException("project id is not setup, please check it !");
			}
			SysDictData host = dictDataService.selectDictDataByTypeAndLabel(loadParams.getProjectId(), "notify_share","notice_probe_address");
			SysDictData port = dictDataService.selectDictDataByTypeAndLabel(loadParams.getProjectId(), "notify_share","notice_probe_port");
			List<ProbeEventMessage> messages = new ArrayList<ProbeEventMessage>();
			ProbeInfo probe = new ProbeInfo();
			probe.setName("事件管理");
			probe.setType(ProbeType.UNKNOW);
			probe.setVersion("1.0");
			probe.setKey("");
			probe.setPort(-1);
			probe.setHeartbeat(-1);
			probe.setThreadCount(-1);
			probe.setTimeout(-1);
			probe.setEventSourceType(EventSourceType.INTERNAL);
			ProjectInfo projectInfo = new ProjectInfo();
			projectInfo.setId(loadParams.getProjectId());
			Map<String, Object> messageBody = FastJsonUtils.stringToCollect(loadParams.getFilter());
			ProbeEventMessage message = new ProbeEventMessage(projectInfo, probe, loadParams.getFilter());
			messages.add(message);
			// todo t_event_notification_log
			// 确认人
			Object acknoledger = messageBody.get("acknoledger");
			// 发送类型
			Object notificationType = messageBody.get("notificationType");
			// 发送时间
			Long millisecond = Instant.now().toEpochMilli();
			// 项目id
			Long projectId = loadParams.getProjectId();
			List<Map<String, Object>> events = (List<Map<String, Object>>) messageBody.get("events");
			for (Map<String, Object> event : events) {
				Object eventID = event.get("EventID");
				Object remark = event.get("remark");
				DataLoadParams dataLoadParams = new DataLoadParams();
				HashMap<Object, Object> filter = new HashMap<>();
				filter.put("event_id", eventID);
				filter.put("acknoledger", acknoledger);
				if (notificationType != null) {
					filter.put("notification_type", notificationType.toString());
				}
				filter.put("transmit_date", millisecond);
				filter.put("remark", remark);
				filter.put("project_id", projectId);
				dataLoadParams.setFilter(JSON.toJSONString(filter));
				dataLoadParams.setDcName("addNotificationLog");
				dataLoadParams.setProjectId(10L);
				dataLoadParams.setStart(1);
				dataLoadParams.setLimit(-10);
				ResultPattern res = DataServiceUtils.dataLoad(dataSource, dataLoadParams);
				if (!res.isSuccess()) {
					logger.error(res.getErrorMsg());
				}
			}
			DispatcherHolder.flushToDelivery(nettyClientId, messages, host.getDictValue(),new Integer(port.getDictValue()));
			resultPattern.setSuccess(true);
			resultPattern.setEmpty(true);
		} catch (Exception e) {
			resultPattern.setDcName(loadParams.getDcName());
			resultPattern.setFilter(loadParams.getFilter());
			resultPattern.setSuccess(false);
			resultPattern.setEmpty(true);
			resultPattern.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		return resultPattern;
	}
}
