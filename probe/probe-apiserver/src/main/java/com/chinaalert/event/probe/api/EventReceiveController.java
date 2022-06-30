package com.chinaalert.event.probe.api;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.domain.ProbeInfo;
import com.chinaalert.event.common.domain.ProjectInfo;
import com.chinaalert.event.probe.inner.EventMessageProducer;

import cn.mcinfotech.data.service.domain.ResultPattern;

/**
 * @author: hy
 * @description: 事件管理
 */
@RestController
@RequestMapping("/event")
/*@Tag(name="事件管理")*/
public class EventReceiveController {
	private static Logger logger=LogManager.getLogger(EventReceiveController.class);
	@Resource
	private EventMessageProducer producer;
	@Autowired
	ProjectInfo project;
	@Autowired
	ProbeInfo probe;
	/**
	 * 以RestApi的方式对外提供事件接收入口
	 * 
	 * @param params
	 * @return
	 */
	/*@Operation(summary="接收事件")*/
	@PostMapping(value = "/receive")
	public ResultPattern receive(@RequestParam String probeKey,@RequestBody String jsonEvent) {
		ResultPattern resultPattern = new ResultPattern();
		try {
			if(logger.isDebugEnabled()){
				logger.debug("received event from probe is "+probeKey+" , message is "+jsonEvent);
			}
			if(!this.probe.getKey().equalsIgnoreCase(probeKey)){
				resultPattern.setSuccess(false);
				resultPattern.setEmpty(true);
				resultPattern.setErrorMsg("receive probe key is incorrect , please check probe configuration and event source's probe key !");
				return resultPattern;
			}
			ProbeEventMessage message = new ProbeEventMessage(project, probe, jsonEvent);
			this.producer.push(message);
			resultPattern.setSuccess(true);
			resultPattern.setEmpty(false);
		} catch (Exception e) {
			resultPattern.setSuccess(false);
			resultPattern.setEmpty(true);
			resultPattern.setErrorMsg(e.getMessage());
			logger.error("receive probe message error !");
			e.printStackTrace();
		}
		return resultPattern;
	}
	@RequestMapping(value = "/test")
	public ResultPattern receive(@RequestParam String probeKey) {
		ResultPattern resultPattern = new ResultPattern();
		try {
			resultPattern.setErrorMsg("[test]no error happened ....");
			resultPattern.setStrData(probeKey);
			resultPattern.setSuccess(true);
			resultPattern.setEmpty(false);
		} catch (Exception e) {
			resultPattern.setSuccess(false);
			resultPattern.setEmpty(true);
			resultPattern.setErrorMsg(e.getMessage());
			logger.error("[test]receive probe message error !");
			e.printStackTrace();
		}
		return resultPattern;
	}
}
