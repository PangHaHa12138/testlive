package com.example.pang.testlive.AlivcLiveRoom;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {

  private static int corePoolSize = 1;
  private static int maximumPoolSize = 1;
  private static int keepAliveTime = 60;

  public static ExecutorService newDynamicSingleThreadedExecutor() {
    ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
            keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    executor.allowCoreThreadTimeOut(true);

    return executor;
  }

}
