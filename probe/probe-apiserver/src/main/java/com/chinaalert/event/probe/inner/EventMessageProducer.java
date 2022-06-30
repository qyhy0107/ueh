package com.chinaalert.event.probe.inner;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.chinaalert.event.common.domain.ProbeEventMessage;

/**
 * 将监控工具来的事件，存入队列
 * @author hy
 */
@Component
public class EventMessageProducer {
	private Logger logger=LogManager.getLogger(EventMessageProducer.class);
	
	public void push(ProbeEventMessage message) throws InterruptedException{
		if (message==null||StringUtils.isEmpty(message.getMessageBody())) {
			logger.warn("no message comming");
			return;
		}
		ProbeInnerConfig.MESSAGEQUEUE.put(message);
	}
}
