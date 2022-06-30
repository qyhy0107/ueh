package com.chinaalert.event.handler.common.inner;

import java.util.List;

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
	public void push(List<ProbeEventMessage> messages){
		InnerConfig.QUEUE.addAll(messages);
	}
}
