package com.chinaalert.event.dispatcher.filter;

import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.filter.IFilter;
import com.chinaalert.event.dispatcher.inner.EventMessageProducer;

import io.netty.channel.ChannelHandlerContext;

/**
 * 接收到从Probe送来的消息之后将Probe送来的消息转发到消息处理器
 * 1.将消息送队列
 * 2.然后根据路由表进行转发
 *
 * @author hy
 */
@Component
@Order(2)
public class ProbeEventMessageFilter implements IFilter<ProbeEventMessage> {
	private Logger logger = LogManager.getLogger(ProbeEventMessageFilter.class);
	@Resource
	private EventMessageProducer producer;
	
	@Override
	public boolean chain(ProbeEventMessage message, ChannelHandlerContext ctx) {
		if(message!=null){
			producer.push(message);
			return false;
		}
		return true;
	}

	@Override
	public boolean chain(List<ProbeEventMessage> message, ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub
		producer.push(message);
		return false;
	}
}
