package com.chinaalert.event.transmit.inner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.domain.ProbeInfo;
import com.chinaalert.event.common.inner.Consumer;
import com.chinaalert.event.common.listener.IListener;
import com.chinaalert.event.common.utils.CpuNum;
import com.chinaalert.event.transmit.domain.TransmitInfo;

/**
 * probe（Transmitter）内部存储事件消息
 * 
 * @author hy
 */
@Configuration
public class TxInnerConfig {
	@Resource
	private IListener<ProbeEventMessage> probeEventMessageListener;
	@Resource
	private TransmitInfo transmitInfo;
	@Autowired
	ProbeInfo probeInfo;

	private ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();
	/**
	 * 队列
	 */
	public static LinkedBlockingQueue<ProbeEventMessage> QUEUE = new LinkedBlockingQueue<>(2000000);
	//public static ConcurrentMap<String,LinkedBlockingQueue<ProbeEventMessage>> TABLE=new ConcurrentHashMap<>();

	/**
	 * 启动消费事件队列
	 * @return
	 */
	@Bean
	public Consumer<EventMessageConsumer> zabbixConsumer() {
		int nowCount = CpuNum.workerCount();
		// 将实际值赋给static变量
		if (this.probeInfo.getThreadCount() != 0) {
			nowCount = this.probeInfo.getThreadCount();
		} else {
			if (nowCount >= 8) {
				nowCount = nowCount / 2;
			}
		}

		List<EventMessageConsumer> consumerList = new ArrayList<>();
		for (int i = 0; i < nowCount; i++) {
			EventMessageConsumer keyConsumer = new EventMessageConsumer();
			keyConsumer.setProbeEventMessageListener(probeEventMessageListener);
			keyConsumer.setTransmitInfo(transmitInfo);
			consumerList.add(keyConsumer);

			threadPoolExecutor.submit(keyConsumer::beginConsume);
		}
		return new Consumer<EventMessageConsumer>(consumerList);
	}
}
