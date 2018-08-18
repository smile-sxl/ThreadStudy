package com.smile.threadstudy.Test;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * author: smile .
 * date: On 2018/8/15
 */
public class Test1 {

    private int queueSize = 10;
    private ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(queueSize);

    class Consumer extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    queue.take();
                    System.out.print("取出首位的内容------");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Producer extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    queue.put(1);
                    System.out.print("往队列中插入------");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {
        Test1 test = new Test1();
        Producer producer = test.new Producer();
        Consumer consumer = test.new Consumer();
        // 两个线程都开始执行  使用了阻塞队列
        producer.start();
        consumer.start();
    }

}
