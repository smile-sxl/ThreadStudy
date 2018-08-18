package com.smile.threadstudy.Test;

import java.util.PriorityQueue;

/**
 * author: smile .
 * date: On 2018/8/15
 */
public class Test {

    private int queueSize = 10;
    private PriorityQueue<Integer> queue = new PriorityQueue<Integer>(queueSize);

    class Consumer extends Thread {
        @Override
        public void run() {
            while (true) {
                // 优先级队列锁
                synchronized (queue) {
                    while (queue.size() == 0) {
                        try {
                            System.out.print("队列空，等待数据");
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            System.out.print("消费者异常---------");
                            queue.notify();
                        }
                    }
                    System.out.print("取出首位的内容------");
                    queue.poll();
                    queue.notify();
                }
            }
        }
    }

    class Producer extends Thread {
        @Override
        public void run() {
            while (true) {
                // 优先级队列锁
                synchronized (queue) {
                    while (queue.size() == queueSize) {
                        try {
                            System.out.print("队列满，等待有空余空间");
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            System.out.print("生产者异常---------");
                            queue.notify();
                        }
                    }
                    System.out.print("往队列中插入------");
                    queue.offer(1);
                    queue.notify();
                }
            }
        }
    }

    public static void main(String[] args) {
        Test test = new Test();
        Producer producer = test.new Producer();
        Consumer consumer = test.new Consumer();
        // 两个线程都开始执行  但因为有优先级队列锁 每次只执行一个线程  但是这两个线程并不是等待另一个线程处于等待状态才执行
        producer.start();
        consumer.start();
    }

}
