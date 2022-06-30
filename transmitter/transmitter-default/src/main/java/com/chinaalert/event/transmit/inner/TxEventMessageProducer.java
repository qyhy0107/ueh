package com.chinaalert.event.transmit.inner;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.handler.common.inner.InnerConfig;


/**
 * 将监控工具来的事件，存入队列,如果队里不存在，则消息丢弃
 * 
 * @author hy
 */
@Component
public class TxEventMessageProducer {
	private Logger logger=LogManager.getLogger(TxEventMessageProducer.class);
	
	public void push(ProbeEventMessage message) {
		if (message==null) {
			logger.warn("no message comming");
			return;
		}
		TxInnerConfig.QUEUE.add(message);
	}
	/**
	 * @param message
	 */
	public void push(List<ProbeEventMessage> message) {
		if (message==null) {
			logger.warn("no message comming");
			return;
		}
		TxInnerConfig.QUEUE.addAll(message);
	}
}
