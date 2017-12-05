package com.gome.pop.fup.easyid.server;

/**
 * Created by fupeng-ds on 2017/8/4.
 */
public class BootstrapTest {

    public static void main(String[] args) throws Exception {
        args = new String[]{"-zookeeper127.0.0.1:2181", "-redis127.0.0.1:6379"};
        Bootstrap.main(args);
    }
}
