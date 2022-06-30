package com.chinaalert.event.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 并发工具类
 * @author q
 *
 */
public class AsyncPool {
	private static ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();

	public static void asyncDo(Runnable runnable) {
		threadPoolExecutor.submit(runnable);
	}

	public static void shutDown() {
		threadPoolExecutor.shutdown();
	}
}
