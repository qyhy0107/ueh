package com.chinaalert.event.probe.inner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.listener.IListener;

/**
 * 事件消费
 * @author hy
 */

public class EventMessageConsumer {
	private Logger logger= LogManager.getLogger(EventMessageConsumer.class);
	private IListener<ProbeEventMessage> eventProbeMessageListener;

	public void setEventProbeMessageListener(IListener<ProbeEventMessage> eventProbeMessageListener) {
		this.eventProbeMessageListener = eventProbeMessageListener;
	}

	public void beginConsume() {
		while (true) {
			try {
				if(!ProbeInnerConfig.MESSAGEQUEUE.isEmpty()){
					/*if(logger.isDebugEnabled()){
						logger.debug("MESSAGEQUEUE size is "+InnerConfig.MESSAGEQUEUE.size());
					}*/
					eventProbeMessageListener.dispatcher(ProbeInnerConfig.MESSAGEQUEUE.take());
				}else {
					Thread.sleep(500);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
