package com.chinaalert.event.probe.inner.listener;

import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.listener.IListener;
import com.chinaalert.event.common.push.IPusher;

/**
 * 将事件推送到事件转发程序
 *
 * @author hy
 */
@Component
public class EventProbeMessageListener implements IListener<ProbeEventMessage> {
	private Logger logger = LogManager.getLogger(EventProbeMessageListener.class);
	@Resource
	private List<IPusher<ProbeEventMessage>> iPushers;

	@Override
	public void dispatcher(ProbeEventMessage message) {
		// 开启推送
		if (logger.isInfoEnabled()) {
			logger.info("push message start ...");
		}
		// 分别推送到各client和etcd
		for (IPusher<ProbeEventMessage> pusher : iPushers) {
			pusher.push(message);
		}
	}

	@Override
	public void dispatcher(String handlerType, List<ProbeEventMessage> message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispatcher(String handlerType, ProbeEventMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispatcher(List<ProbeEventMessage> message) {
		// TODO Auto-generated method stub
		
	}

}
