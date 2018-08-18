package com.smile.threadstudy.Test;

import java.util.concurrent.TimeUnit;

/**
 * author: smile .
 * date: On 2018/8/15
 */
public class StopThread {

    public static class MoonRunner implements Runnable {
        private long i;

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                i++;
                System.out.println(i);
            }
            System.out.print("stop");
        }
    }

    public static void main(String [] args) throws InterruptedException {
        MoonRunner runnable = new MoonRunner();
        Thread moonthread = new Thread(runnable, "MonnThread");
        moonthread.start();
        TimeUnit.MILLISECONDS.sleep(10);
        moonthread.interrupt();
    }


}
