package com.gome.pop.fup.easyid.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by fupeng-ds on 2017/8/3.
 */
public class IpUtil {

    public static String getHost(String ip) {
        String[] split = ip.split(":");
        return split[0];
    }

    public static int getPort(String ip) {
        String[] split = ip.split(":");
        return Integer.valueOf(split[1]);
    }

    public static String getLocalHost() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
}
