package com.fytu.pigmediastudio.tts;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * create by FengyiTu at 2019.8.9
 */

public class PigBlockQueue<T> {
    private static final String TAG = "PigBlockQueue";
    /**
     * 队列中的容器，用来放队列的元素
     */
    private final List<T> list = new ArrayList<>();
    /**
     * 队列的最大容量值
     */
    private final int maxSize;
    /**
     * 队列当前的元素个数
     */
    private int size = 0;
    /**
     * 锁
     */
    private final Object object = new Object();

    /**
     * 对应tts是否结束
     */
    private boolean isValid = true;

    public PigBlockQueue(int maxSize) {
        this.maxSize = maxSize;
    }


    //无限个
    public PigBlockQueue() {
        maxSize = Integer.MAX_VALUE;
    }

    /**
     * 插入一个元素到队列里，如果空间不足，则等待，直到有空间位置
     *
     * @param t the element to add
     * @throws InterruptedException if interrupted while waiting
     */
    public void put(T t) throws InterruptedException {
        synchronized (object) {
            Log.d(TAG,"put");
            while (size == maxSize && isValid) {
                object.wait();
            }
            list.add(t);
            size++;
            object.notify();
        }
    }

    /**
     * 移除队列中第一个元素，如果当前队列没有元素，则一直等待，直到有元素位置。
     *
     * @return the head of this queue
     * @throws InterruptedException if interrupted while waiting
     */
    public T take() throws InterruptedException {
        T t;
        synchronized (object) {
            Log.d(TAG,"take");
            while (size == 0 && isValid) {
                object.wait();
            }
            if (!isValid){
                object.notify();
                return null;
            }
            t = list.remove(0);
            size--;
            object.notify();
        }
        return t;
    }


    public void removeElementByEqual(T string) {
        if(string instanceof String) {
            synchronized (object) {
                Log.d(TAG, "remove by equal");
                while (true) {
                    if (!list.remove(string)) {
                        break;
                    } else {
                        //成功删除
                        size--;
                    }
                }
                object.notify();
            }
        }
    }

    public void removeElementByContains(T string) {

        if (string instanceof String) {
            synchronized (object) {
                Log.d(TAG, "remove by contains");
//            while (true){
                for (int i = list.size() - 1; i >= 0; i--) {
                    if ((((String)list.get(i)).contains((String)string))){
                        list.remove(i);
                        size--;
                    }
                }
//            }
                object.notify();
            }
        }
    }

    public void removeElementByIndex(int index) {
        synchronized (object) {
            Log.d(TAG, "remove by index");
            if (list.size() == 0){

            }else {
                if (index >= list.size()) {
                    list.remove(list.size() - 1);
                } else {
                    list.remove(index);
                }
                size--;
            }
            object.notify();
        }
    }


    /**
     * 倒数第一个 |countIndex| = 1
     *
     * @param countIndex
     */
    public void removeElementByCountIndex(int countIndex) {
        synchronized (object) {

            Log.d(TAG, "remove by countIndex");
            if (list.size()==0){

            }else {
                if (countIndex < 0) {
                    list.remove(list.size() + countIndex);
                } else if (countIndex > 0) {
                    list.remove(list.size() - countIndex);
                } else {
                    list.remove(list.size() - 1);
                }
                size--;
            }
            object.notify();
        }
    }

    public void removeAllElement() {
        synchronized (object) {
            Log.d(TAG, "remove all");
            list.clear();
            object.notify();
        }
    }

    public void release(){
        isValid = false;
    }

}
