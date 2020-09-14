package com.jd.platform.hotkey.dashboard.common.monitor;


import cn.hutool.core.date.SystemClock;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 滑动窗口
 */
public class SlidingWindow {
    /**
     * 循环队列，就是装多个窗口用，该数量是windowSize的2倍
     */
    private AtomicInteger[] timeSlices;
    /**
     * 队列的总长度
     */
    private int timeSliceSize;
    /**
     * 每个时间片的时长，以毫秒为单位
     */
    private int timeMillisPerSlice;
    /**
     * 共有多少个时间片（即窗口长度）
     */
    private int windowSize;
    /**
     * 在一个完整窗口期内允许通过的最大阈值
     */
    private int threshold;
    /**
     * 该滑窗的起始创建时间，也就是第一个数据
     */
    private long beginTimestamp;
    /**
     * 最后一个数据的时间戳
     */
    private long lastAddTimestamp;

    public static void main(String[] args) {
        //1秒一个时间片，窗口共5个
        SlidingWindow window = new SlidingWindow(10, 20);

        CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                int hot = window.addCount(2);
                System.out.println(hot);
            }).start();
        }
    }


    public SlidingWindow(int windowSize, int threshold) {
        this.timeMillisPerSlice = 60 * 1000;
        this.windowSize = windowSize;
        this.threshold = threshold;
        // 保证存储在至少两个window
        this.timeSliceSize = windowSize * 2;
        reset();
    }

    /**
     * 初始化
     */
    private void reset() {
        beginTimestamp = SystemClock.now();
        //窗口个数
        AtomicInteger[] localTimeSlices = new AtomicInteger[timeSliceSize];
        for (int i = 0; i < timeSliceSize; i++) {
            localTimeSlices[i] = new AtomicInteger(0);
        }
        timeSlices = localTimeSlices;
    }


    private int locationIndex() {
        long now = SystemClock.now();
        if (now - lastAddTimestamp > timeMillisPerSlice * windowSize) { reset(); }
        int index = (int) (((now - beginTimestamp) / timeMillisPerSlice) % timeSliceSize);
        return Math.max(index, 0);
    }


    public synchronized int addCount(int count) {
        int index = locationIndex();
        clearFromIndex(index);
        int sum = 0;
        // 在当前时间片里继续+1
        sum += timeSlices[index].addAndGet(count);
        //加上前面几个时间片
        for (int i = 1; i < windowSize; i++) {
            sum += timeSlices[(index - i + timeSliceSize) % timeSliceSize].get();
        }
        lastAddTimestamp = SystemClock.now();
        if(sum >= threshold){
            return sum;
        }
        return 0;
    }

    private void clearFromIndex(int index) {
        for (int i = 1; i <= windowSize; i++) {
            int j = index + i;
            if (j >= windowSize * 2) {
                j -= windowSize * 2;
            }
            timeSlices[j].set(0);
        }
    }


    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
