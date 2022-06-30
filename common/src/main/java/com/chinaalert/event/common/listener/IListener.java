package com.chinaalert.event.common.listener;

import java.util.List;

/**
 * @author hy
 */
public interface IListener<T> {
	/**
	 * 新来一个事件
	 */
	void dispatcher(T message);
	void dispatcher(List<T> messages);
	void dispatcher(String handlerType,T message);
	void dispatcher(String handlerType,List<T> message);
}
