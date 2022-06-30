package com.chinaalert.event.handler.common.inner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.listener.IListener;
import com.chinaalert.event.handler.common.domain.DeliveryTableInfo;
import com.google.common.collect.Queues;

/**
 * 事件消费
 * @author hy
 */

public class EventMessageConsumer {
	private Logger logger=LogManager.getLogger(EventMessageConsumer.class);
	private IListener<ProbeEventMessage> eventProbeMessageListener;
	private DeliveryTableInfo deliveryTable;
	
	public void setEventProbeMessageListener(IListener<ProbeEventMessage> eventProbeMessageListener) {
		this.eventProbeMessageListener = eventProbeMessageListener;
	}

	public void setDeliveryTable(DeliveryTableInfo deliveryTable) {
		this.deliveryTable = deliveryTable;
	}

	public void beginConsume() {
		while (true) {
			try {
				/*ProbeEventMessage model = InnerConfig.QUEUE.take();
				for(String handlerType:deliveryTable.getDelivery().keySet()){
					if(logger.isDebugEnabled()){
						logger.debug("push message , via "+deliveryTable.getDelivery().get(handlerType));
					}
					eventProbeMessageListener.dispatcher(handlerType,model);
				}*/
				
				List<ProbeEventMessage> messages=new ArrayList<ProbeEventMessage>();
				Queues.drain(InnerConfig.QUEUE, messages, 10, 60, TimeUnit.SECONDS);
					/*if(logger.isDebugEnabled()){
						logger.debug("fetch "+handlerType+"'s message , size is "+messages.size());
					}*/
				for(String handlerType:deliveryTable.getDelivery().keySet()){
					if(messages.size()>0){
						eventProbeMessageListener.dispatcher(handlerType,messages);
					}
				}
				/*}*/
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
