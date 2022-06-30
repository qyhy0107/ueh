package com.chinaalert.event.dispatcher.inner;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.chinaalert.event.common.domain.ProbeEventMessage;



/**
 * 将监控工具来的事件，存入队列,如果队里不存在，则消息丢弃
 * 
 * @author hy
 */
@Component
public class EventMessageProducer {
	private Logger logger=LogManager.getLogger(EventMessageProducer.class);
	
	public void push(ProbeEventMessage message) {
		if (message==null) {
			logger.warn("no message comming");
			return;
		}
		try {
			if(InnerConfig.TABLE.containsKey(message.getProbe().getEventSourceType().name())){
				InnerConfig.TABLE.get(message.getProbe().getEventSourceType().name()).put(message);
			}else{
				logger.error("probe[dispatcher]no config of "+message.getProbe().getEventSourceType().name()+" , please check it ...");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 暂时没有用到
	 * @param message
	 */
	public void push(List<ProbeEventMessage> message) {
	}
}
