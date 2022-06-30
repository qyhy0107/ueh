package com.chinaalert.event.common.netty.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Probe请求Dispatcher的客户端处理程序
 * 1.通过userEventTriggered发送心跳消息HeartBeatMessage
 * 2.通过channelRead0读取Dispatcher回复的信息
 * @author hy
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
	private Logger logger = LogManager.getLogger(NettyClientHandler.class);
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, cause);
		if(logger.isDebugEnabled()){
			logger.debug("something happen :"+cause.getMessage());
		}
	}
}
