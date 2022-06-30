package com.chinaalert.event.handler.common.inner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.domain.ProbeInfo;
import com.chinaalert.event.common.inner.Consumer;
import com.chinaalert.event.common.listener.IListener;
import com.chinaalert.event.common.utils.CpuNum;
import com.chinaalert.event.handler.common.domain.DeliveryTableInfo;

/**
 * probe内部存储事件消息
 * 
 * @author hy
 */
@Configuration
public class InnerConfig {
	@Resource
	private IListener<ProbeEventMessage> eventProbeMessageListener;

	private ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();

	@Resource
	ProbeInfo probeInfo;
	@Resource
	DeliveryTableInfo deliveryTableInfo;
	/**
	 * 队列
	 */
	public static LinkedBlockingQueue<ProbeEventMessage> QUEUE = new LinkedBlockingQueue<>(2000000);

	@Bean
	/**
	 * 启动消费事件队列
	 * @return
	 */
	public Consumer<EventMessageConsumer> consumer() {
		int nowCount = CpuNum.workerCount();
		// 将实际值赋给static变量
		if (probeInfo.getThreadCount() != 0) {
			nowCount = probeInfo.getThreadCount();
		} else {
			if (nowCount >= 8) {
				nowCount = nowCount / 2;
			}
		}

		List<EventMessageConsumer> consumerList = new ArrayList<>();
		for (int i = 0; i < nowCount; i++) {
			EventMessageConsumer keyConsumer = new EventMessageConsumer();
			keyConsumer.setEventProbeMessageListener(eventProbeMessageListener);
			keyConsumer.setDeliveryTable(deliveryTableInfo);
			consumerList.add(keyConsumer);

			threadPoolExecutor.submit(keyConsumer::beginConsume);
		}
		return new Consumer<EventMessageConsumer>(consumerList);
	}
}
