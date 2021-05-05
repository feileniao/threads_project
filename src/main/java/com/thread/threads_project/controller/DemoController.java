package com.thread.threads_project.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @date 2021/5/4
 * @description 多线程实例
 */
@RestController
@RequestMapping("thread")
public class DemoController {

    @Qualifier("threadPool")
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @RequestMapping("/test")
    public void get() {
        System.out.println(Thread.currentThread().getId() + "-" + Thread.currentThread().getName() + "---主线程任务");
        List<Integer> list = new ArrayList();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        System.out.println(list.size());

        int count = 50;
        int listSize = list.size();
        //线程数
        int group = listSize / count;
        System.out.println("group:" + group);
        List<Integer> newList = null;
        Long start = System.currentTimeMillis();
        for (int i = 0; i < group; i++) {
            int startIndex = i * count;
            int endIndex = (i + 1) * count;
            newList = list.subList(startIndex, endIndex);
//            newList.stream().forEach(System.out::println);
            List<Integer> finalNewList = newList;
//            finalNewList.stream().forEach(System.out::println);
            threadPoolExecutor.submit(() -> {
//                System.out.println(Thread.currentThread().getId() + "-" + Thread.currentThread().getName() + "---任务");
                for (int a = 0; a < finalNewList.size(); a++) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    System.out.println(Thread.currentThread().getId() + "-" + Thread.currentThread().getName() + "---" + finalNewList.get(a));
                }
            });
        }
        Long end = System.currentTimeMillis();
        System.out.println((end-start)/1000);
        System.out.println("--------------------------");

        Long start2 = System.currentTimeMillis();
        for (int i = 0; i < list.size(); i++) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//                    System.out.println(Thread.currentThread().getId() + "-" + Thread.currentThread().getName() + "---" + list.get(i));
        }
        Long end2 = System.currentTimeMillis();
        System.out.println(end2);
        System.out.println((end2-start2)/1000);
    }

}
