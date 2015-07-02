package com.changhong.tvserver.touying.image.loader.task;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * this class is used for create task execute pool for image download
 *
 * Created by Jack Wang
 */
public class TaskExecutorFactory {

    private final static int DEFAULT_THREAD_NUMBER = 3;

    private final static int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY + 1;

    public static Executor createdDownloadExecutor() {
        BlockingQueue<Runnable> taskQueue =  new LinkedBlockingQueue<Runnable>();
        return new ThreadPoolExecutor(DEFAULT_THREAD_NUMBER, DEFAULT_THREAD_NUMBER, 0L, TimeUnit.MILLISECONDS,
                taskQueue, createThreadFactory(DEFAULT_THREAD_PRIORITY, "dl-pool-"));
    }

    public static Executor createdDisplayExecutor() {
        BlockingQueue<Runnable> taskQueue =  new LinkedBlockingQueue<Runnable>();
        return new ThreadPoolExecutor(DEFAULT_THREAD_NUMBER, DEFAULT_THREAD_NUMBER, 0L, TimeUnit.MILLISECONDS,
                taskQueue, createThreadFactory(DEFAULT_THREAD_PRIORITY, "ui-pool-"));
    }

    private static ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
        return new DefaultThreadFactory(threadPriority, threadNamePrefix);
    }

    private static class DefaultThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) t.setDaemon(false);
            t.setPriority(threadPriority);
            return t;
        }
    }
}
