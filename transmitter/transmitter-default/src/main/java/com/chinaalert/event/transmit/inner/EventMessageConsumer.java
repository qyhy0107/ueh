package com.chinaalert.event.transmit.inner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.listener.IListener;
import com.chinaalert.event.transmit.domain.TransmitInfo;
import com.google.common.collect.Queues;

/**
 * 事件消费
 * 按照事件源类型进行消费，消费策略：按数量或者时间周期进行消费，任何一个先到都会触发消费
 * @author hy
 */

public class EventMessageConsumer {
	private Logger logger=LogManager.getLogger(EventMessageConsumer.class);
	private IListener<ProbeEventMessage> probeEventMessageListener;
	private TransmitInfo transmitInfo;
	
	public void beginConsume() {
		while (true) {
			try {
				List<ProbeEventMessage> messages=new ArrayList<ProbeEventMessage>();
				Queues.drain(TxInnerConfig.QUEUE, messages, transmitInfo.getBatch(), transmitInfo.getInterval(), TimeUnit.SECONDS);
				
				if(messages.size()>0){
					probeEventMessageListener.dispatcher(messages);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void setProbeEventMessageListener(IListener<ProbeEventMessage> eventProbeMessageListener) {
		this.probeEventMessageListener = eventProbeMessageListener;
	}
	public TransmitInfo getTransmitInfo() {
		return transmitInfo;
	}
	public void setTransmitInfo(TransmitInfo transmitInfo) {
		this.transmitInfo = transmitInfo;
	}
}
