package com.chinaalert.event.common.push;

import java.util.List;

/**
 * @author hy
 */
public interface IPusher<T> {
	void push(T message);
	void push(List<T> messages);
	void push(String handlerType,T messages);
	void push(String handlerType,List<T> messages);
}
