package com.thread.threads_project.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池
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

    /**
     * 初始化
     */
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
                });
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

    /**
     * 监控进程
     */
    @PostConstruct
    public void exitMonitor() {
        Thread exit = new Thread(() -> {
            // 保证程序正常执行
            try {
                TimeUnit.MINUTES.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 检查是否执行完毕
            for (; ; ) {
                try {
                    int poolSize = threadPoolExecutor.getPoolSize();
                    int queueSize = threadPoolExecutor.getQueue().size();
                    log.info("当前线程数：{}，等待执行的任务：{}", poolSize, queueSize);
                    TimeUnit.MINUTES.sleep(30);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        });
        exit.start();
    }
}
