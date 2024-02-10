package io.github.teamlead.net;

/**
 * A utility class providing static methods for converting IP addresses between different formats.
 * This class includes methods for converting IP addresses from quad-dotted string representation
 * to byte array and integer formats, and vice versa. It is designed with private constructor
 * to prevent instantiation, as it is intended to be used in a static context.
 */
public class Utils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Utils() {

    }

    /**
     * Converts an IP address from its quad-dotted string representation to a byte array.
     * Each element in the byte array represents one part of the IP address.
     *
     * @param ipStr A quad-dotted notation string, e.g., "192.168.5.25"
     * @return A byte array representing the IP address, where each byte corresponds to a part of the IP address.
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
                b += d;
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
     * Converts an IP address from its quad-dotted string representation to an integer.
     * This method encapsulates the IP address into a single integer value, where each byte of the integer
     * represents one part of the IP address.
     *
     * @param ipStr A quad-dotted notation string, e.g., "192.168.5.25"
     * @return An integer representing the IP address.
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
     * Converts an IP address from its integer representation back to a quad-dotted string format.
     * This method decodes the integer into its constituent parts to form the IP address string.
     *
     * @param ip An integer representing the IP address.
     * @return A quad-dotted notation string representing the IP address, e.g., "192.168.5.25".
     */
    public static String intToIpAddr(int ip) {

        return ((ip) >>> 24) +
               "." +
               ((ip & 0x00FFFFFF) >>> 16) +
               "." +
               ((ip & 0x0000FFFF) >>> 8) +
               "." +
               (ip & 0x000000FF);
    }
}
