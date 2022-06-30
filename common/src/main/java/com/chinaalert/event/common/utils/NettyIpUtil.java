package com.chinaalert.event.common.utils;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * @author hy
 */
public class NettyIpUtil {
	/**
	 * 从netty连接中读取ip地址
	 */
	public static String clientIp(ChannelHandlerContext ctx) {
		try {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			return insocket.getAddress().getHostAddress();
		} catch (Exception e) {
			return "未知";
		}

	}
}
