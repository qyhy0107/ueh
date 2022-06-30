package com.chinaalert.event.dispatcher.inner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
import com.chinaalert.event.dispatcher.domain.DispatcherInfo;
import com.chinaalert.event.dispatcher.domain.RoutingTable;


/**
 * probe（Dispatcher）内部存储事件消息
 * 
 * @author hy
 */
@Configuration
public class InnerConfig {
	@Resource
	private IListener<ProbeEventMessage> probeEventMessageListener;
	@Resource
	private DispatcherInfo dispatcherInfo;
	@Autowired
	ProbeInfo probeInfo;
	@Autowired
	RoutingTable RoutingTableInfo;
	private ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();
	/**
	 * 队列
	 */
	//public static LinkedBlockingQueue<ProbeEventMessage> QUEUE = new LinkedBlockingQueue<>(2000000);
	public static ConcurrentMap<String,LinkedBlockingQueue<ProbeEventMessage>> TABLE=new ConcurrentHashMap<>();
	//public static ConcurrentMap<String,ConcurrentMap<String,LinkedBlockingQueue<ProbeEventMessage>>> TABLE=new ConcurrentHashMap<>();
	/**
	 * 按照配置的dispatcher路由进行dispatcher存储初始化
	 * @return
	 */
	@Bean
	public boolean initilizeTable(){
		for(String handlerType:this.RoutingTableInfo.getRoutingTable().keySet()){
			InnerConfig.TABLE.put(handlerType, new LinkedBlockingQueue<>(2000000));
			//InnerConfig.TABLE.clear();[
		}
		return true;
	}
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
			keyConsumer.setRoutingTable(RoutingTableInfo);
			keyConsumer.setDispatcherInfo(dispatcherInfo);
			consumerList.add(keyConsumer);

			threadPoolExecutor.submit(keyConsumer::beginConsume);
		}
		return new Consumer<EventMessageConsumer>(consumerList);
	}
}
