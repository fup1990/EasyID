package com.gome.pop.fup.easyid.demo;

import com.gome.pop.fup.easyid.id.EasyID;

/**
 * Created by fupeng-ds on 2017/8/4.
 */
public class Main {

    public static void main(String[] args) {
        final EasyID easyID = new EasyID();
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    long id = easyID.nextId();
                    System.out.println("id:" + id + ",timestamp:" + System.currentTimeMillis());
                }
            });
            thread.start();
//            long id = easyID.nextId();
//            System.out.println("id:" + id + ",timestamp:" + System.currentTimeMillis());
        }
    }


}
