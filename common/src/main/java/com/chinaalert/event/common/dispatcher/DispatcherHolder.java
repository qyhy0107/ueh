package com.chinaalert.event.common.dispatcher;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.netty.client.NettyClient;

import io.netty.buffer.ByteBuf;

/**
 * 将Probe消息flush到Dispatcher
 * @author hy
 */
public class DispatcherHolder {
	private static Logger logger=LogManager.getLogger(DispatcherHolder.class);

	public static void flushToDispatcher(String channelName,ByteBuf message,String dispatcherHost,int port) {
		if(logger.isDebugEnabled()){
			logger.debug("message flush to "+dispatcherHost+" ,via port "+port+"start ....");
		}
		NettyClient.getInstance().writeAndFlushMessage(channelName,dispatcherHost,port,message);
		if(logger.isDebugEnabled()){
			logger.debug("message flush to "+dispatcherHost+" ,via port "+port+" end ...");
		}
	}
	
	public static void flushToDispatcher(String channelName,ProbeEventMessage message,String dispatcherHost,int port) {
		if(logger.isDebugEnabled()){
			logger.debug("message flush to "+dispatcherHost+" ,via port "+port+"start ....");
		}
		NettyClient.getInstance().writeAndFlushMessage(channelName,dispatcherHost,port,message);
		if(logger.isDebugEnabled()){
			logger.debug("message flush to "+dispatcherHost+" ,via port "+port+" end ...");
		}
	}
	
	public static void flushToDispatcher(String channelName,List<ProbeEventMessage> message,String dispatcherHost,int port) {
		if(logger.isDebugEnabled()){
			logger.debug("message flush to "+dispatcherHost+" ,via port "+port+"start ....");
		}
		NettyClient.getInstance().writeAndFlushMessage(channelName,dispatcherHost,port,message);
		if(logger.isDebugEnabled()){
			logger.debug("message flush to "+dispatcherHost+" ,via port "+port+" end ...");
		}
	}
	public static void flushToDelivery(String channelName,ProbeEventMessage message,String dispatcherHost,int port) {
		if(logger.isDebugEnabled()){
			logger.debug("message flush to "+dispatcherHost+" ,via port "+port+"start ....");
		}
		NettyClient.getInstance().writeAndFlushMessage(channelName,dispatcherHost,port,message);
		if(logger.isDebugEnabled()){
			logger.debug("message flush to "+dispatcherHost+" ,via port "+port+" end ...");
		}
	}
	public static void flushToDelivery(String channelName,List<ProbeEventMessage> message,String dispatcherHost,int port) {
		if(logger.isDebugEnabled()){
			logger.debug("message flush to "+dispatcherHost+" ,via port "+port+"start ....");
		}
		NettyClient.getInstance().writeAndFlushMessage(channelName,dispatcherHost,port,message);
		if(logger.isDebugEnabled()){
			logger.debug("message flush to "+dispatcherHost+" ,via port "+port+" end ...");
		}
	}
}
