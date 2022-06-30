package com.chinaalert.event.probe.pusher;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.chinaalert.event.common.config.ProbeInfoConfig;
import com.chinaalert.event.common.dispatcher.DispatcherHolder;
import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.push.IPusher;

/**
 * 推送消息到Dispatcher
 * 需要实现将消息推送到Dispatcher时如果遇到网络不通或者dispatcher异常是否需要将事件缓存起来？待恢复通讯之后再进行重发
 * @author hy
 */
@Component
public class EventProbeMessageDispatcherPusher implements IPusher<ProbeEventMessage> {
	private static Logger logger=LogManager.getLogger(EventProbeMessageDispatcherPusher.class);
	@Value("${dispatcher.host}")
	private String host;
	@Value("${dispatcher.port}")
	private int port;
	@Value("${dispatcher.enable}")
	private boolean enable;
	
	private AtomicLong messageCount=new AtomicLong(0);
	
	@Override
	public void push(ProbeEventMessage message) {
		if(this.enable){
			if(logger.isDebugEnabled()){
				logger.debug("pusher start to work and push message to "+host+" via port "+port);
			}
			DispatcherHolder.flushToDispatcher(ProbeInfoConfig.NEXTCHANNELNAME,message,host,port);
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("push no start , message transmit has stopped ."+messageCount.incrementAndGet());
			}
		}
	}
	@Override
	public void push(String handlerType, List<ProbeEventMessage> messages) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void push(String handlerType, ProbeEventMessage messages) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void push(List<ProbeEventMessage> message) {
		// TODO Auto-generated method stub
		
	}
}
