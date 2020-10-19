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
    private int max;

    /**
     * 在一个完整窗口期内最小阈值
     */
    private int min;
    /**
     * 该滑窗的起始创建时间，也就是第一个数据
     */
    private long beginTimestamp;
    /**
     * 最后一个数据的时间戳
     */
    private long lastAddTimestamp;


    public SlidingWindow(int windowSize, int min, int max) {
        this.timeMillisPerSlice = 60 * 1000;
        this.windowSize = windowSize;
        this.max = max;
        this.min = min;
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
        if(sum >= max){
            return sum;
        }else if(sum < min){
            return -1;
        }else{
            return 0;
        }
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

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
