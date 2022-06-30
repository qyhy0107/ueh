package com.chinaalert.event.dispatcher.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.filter.IFilter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 处理从Probe送来的消息
 *
 * @author hy
 */
public class ReceiverServerHandler extends ChannelInboundHandlerAdapter {
	private Logger logger = LogManager.getLogger(getClass());
	/**
	 * 请自行维护Filter的添加顺序
	 */
	private List<IFilter<ProbeEventMessage>> messageFilters = new ArrayList<>();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object message) {
		if (StringUtils.isEmpty(message)) {
			logger.warn("no message comming .");
			return;
		}
		if(logger.isDebugEnabled()){
			logger.debug("message incoming : "+message);
		}
		ProbeEventMessage probeMessage=(ProbeEventMessage)message;
		/**
		 * 对送来的消息进行过滤
		 */
		for (IFilter<ProbeEventMessage> messageFilter : messageFilters) {
			boolean doNext = messageFilter.chain(probeMessage, ctx);
			if (!doNext) {
				return;
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		logger.error("some thing is error , " + cause.getMessage());
	}

	public void addMessageFilter(IFilter<ProbeEventMessage> iNettyMsgFilter) {
		if (iNettyMsgFilter != null) {
			messageFilters.add(iNettyMsgFilter);
		}
	}

	public void addMessageFilters(List<IFilter<ProbeEventMessage>> iNettyMsgFilters) {
		if (!CollectionUtils.isEmpty(iNettyMsgFilters)) {
			messageFilters.addAll(iNettyMsgFilters);
		}
	}
}
