package com.thread.threads_project.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池配置参数
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {
    /**
     * 核心线程数
     */
    @Value("${thread.pool.core-size}")
    private int coreSize;
    /**
     * 最大线程数
     */
    @Value("${thread.pool.max-size}")
    private int maxSize;
    /**
     * 存活时间
     */
    @Value("${thread.pool.keep-alive-time}")
    private int keepAliveTime;
    /**
     * 队列容量
     */
    @Value("${thread.pool.block-queue.capacity}")
    private int capacity;

    /**
     * 原子递增
     */
    private final AtomicInteger threadNO = new AtomicInteger(0);

    @Qualifier("threadPool")
    @Resource
    @Lazy
    private ThreadPoolExecutor threadPoolExecutor;

    @Bean(name = "threadPool")
    public ThreadPoolExecutor threadPoolExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                coreSize,
                maxSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(capacity),
                r -> {
                    Thread t = new Thread(r);
                    t.setName("thread-tools-" + threadNO.addAndGet(1));
                    return t;
                },
                new ThreadPoolExecutor.AbortPolicy()
        );
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

}
