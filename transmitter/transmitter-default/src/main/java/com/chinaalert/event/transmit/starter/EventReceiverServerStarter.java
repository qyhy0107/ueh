package com.chinaalert.event.transmit.starter;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.domain.ProbeInfo;
import com.chinaalert.event.common.filter.IFilter;
import com.chinaalert.event.common.utils.AsyncPool;
import com.chinaalert.event.transmit.server.ReceiverServer;

/**
 * Dispatcher接收器启动
 * @author hy
 */
@Component
public class EventReceiverServerStarter {
	private Logger logger = LogManager.getLogger(EventReceiverServerStarter.class);

	@Resource
	private List<IFilter<ProbeEventMessage>> messageFilters;

	@Resource
	private ProbeInfo probe;

	@PostConstruct
	public void start() {
		AsyncPool.asyncDo(() -> {
			if(logger.isInfoEnabled()){
				logger.info("event receiver is starting");
			}

			ReceiverServer receiverServer = new ReceiverServer();
			receiverServer.setMessageFilters(messageFilters);
			//receiverServer.setProbe(probe);
			try {
				receiverServer.startNettyServer(this.probe.getPort());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
