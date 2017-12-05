package com.gome.pop.fup.easyid.util;

/**
 * 类型转换工具类
 * Created by fupeng-ds on 2017/8/3.
 */
public class ConversionUtil {

    /**
     * 字节数组转化成int类型
     * @param b
     * @return
     */
    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    /**
     * int转换成字节数组
     * @param a
     * @return
     */
    public static byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static String[] longsToStrings(long[] longs) {
        String[] strs = new String[longs.length];
        for (int i = 0; i < longs.length; i++) {
            strs[i] = String.valueOf(longs[i]);
        }
        return strs;
    }
}
