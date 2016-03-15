package com.github.x25.net;

public class Utils {

    /**
     * @param ipStr A quad-dotted notation string, e.g. "192.168.5.25"
     * @return byte[]
     */
    public static byte[] ipAddrToBytes(String ipStr) {
        byte[] result = new byte[4];
        long b = 0L;
        int q = 0;

        if (ipStr.length() <= 0 || ipStr.length() > 15) {
            return null;
        }

        for (int i = 0; i < ipStr.length(); ++i) {

            if (ipStr.charAt(i) == 46) {
                if (b < 0L || b > 255L || q == 3) {
                    return null;
                }

                result[q++] = (byte) ((int) (b & 255L));
                b = 0L;
            } else {
                int d = Character.digit(ipStr.charAt(i), 10);
                if (d < 0) {
                    return null;
                }

                b *= 10L;
                b += (long) d;
            }
        }

        if (b >= 0L && b < 1L << (4 - q) * 8) {
            switch (q) {
                case 0:
                    result[0] = (byte) ((int) (b >> 24 & 255L));
                case 1:
                    result[1] = (byte) ((int) (b >> 16 & 255L));
                case 2:
                    result[2] = (byte) ((int) (b >> 8 & 255L));
                case 3:
                    result[3] = (byte) ((int) (b & 255L));
                default:
                    return result;
            }
        } else {
            return null;
        }
    }

    /**
     * @param ipStr A quad-dotted notation string, e.g. "192.168.5.25"
     * @return long
     */
    public static int ipAddrToInt(String ipStr) {
        byte[] bytes = ipAddrToBytes(ipStr);

        if (bytes == null) {
            throw new IllegalArgumentException("Could not parse [" + ipStr + "]");
        }

        int ret = 0;
        for (int i = 0; i < 4 && i < bytes.length; i++) {
            ret <<= 8;
            ret |= (int) bytes[i] & 0xFF;
        }
        return ret;
    }

    /**
     * @param ip A quad-dotted notation string, e.g. "192.168.5.25"
     * @return String
     */
    public static String intToIpAddr(int ip) {

        return "" + String.valueOf((ip) >>> 24) +
                "." +
                String.valueOf((ip & 0x00FFFFFF) >>> 16) +
                "." +
                String.valueOf((ip & 0x0000FFFF) >>> 8) +
                "." +
                String.valueOf(ip & 0x000000FF);
    }
}
