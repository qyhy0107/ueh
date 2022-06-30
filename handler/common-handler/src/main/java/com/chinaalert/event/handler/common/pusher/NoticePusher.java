package com.chinaalert.event.handler.common.pusher;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.chinaalert.event.common.dispatcher.DispatcherHolder;
import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.push.IPusher;
import com.chinaalert.event.handler.common.domain.DeliveryInfo;
import com.chinaalert.event.handler.common.domain.DeliveryTableInfo;

/**
 * 通知转发
 * @author hy
 */
@Component
public class NoticePusher implements IPusher<ProbeEventMessage> {
	private static Logger logger=LogManager.getLogger(NoticePusher.class);
	@Resource
	DeliveryTableInfo deliveryInfo;
	
	private AtomicLong messageCount=new AtomicLong(0);
	@Override
	public void push(ProbeEventMessage message) {
	}
	@Override
	public void push(String handlerType,List<ProbeEventMessage> messages) {
		DeliveryInfo routingInfo=this.deliveryInfo.getDelivery().get(handlerType);
		if(routingInfo!=null&&routingInfo.isEnable()){
			if(logger.isDebugEnabled()){
				logger.debug("pusher start to work and push message to "+routingInfo.getHost()+" via port "+routingInfo.getPort());
			}
			messageCount.set(0l);
			DispatcherHolder.flushToDelivery(routingInfo.getType(),messages,routingInfo.getHost(),routingInfo.getPort());
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("notice not start , message transmit stop ."+messageCount.incrementAndGet());
			}
		}
	}

	@Override
	public void push(String handlerType, ProbeEventMessage message) {
		DeliveryInfo routingInfo=this.deliveryInfo.getDelivery().get(handlerType);
		if(routingInfo!=null&&routingInfo.isEnable()){
			if(logger.isDebugEnabled()){
				logger.debug("pusher start to work and push message to "+routingInfo.getHost()+" via port "+routingInfo.getPort());
			}
			messageCount.set(0l);
			DispatcherHolder.flushToDelivery(routingInfo.getType(),message,routingInfo.getHost(),routingInfo.getPort());
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("notice not start , message transmit stop ."+messageCount.incrementAndGet());
			}
		}
	}
	@Override
	public void push(List<ProbeEventMessage> messages) {
		// TODO Auto-generated method stub
		
	}
}
