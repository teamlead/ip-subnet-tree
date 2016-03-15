package com.github.x25.net.tree;

import com.github.x25.net.Utils;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IpSubnetTree<V> {

    private RadixInt32Tree<V> tree;
    private V defaultValue;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private void acquireWriteLock() {
        readWriteLock.writeLock().lock();
    }

    private void releaseWriteLock() {
        readWriteLock.writeLock().unlock();
    }

    public IpSubnetTree() {
        tree = new RadixInt32Tree<V>();
    }

    public V getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(V value) {
        defaultValue = value;
    }

    /**
     * @param block eg. 24
     * @return eg. 0.0.0.255
     */
    private static int getMaskByBlock(long block) {
        return block > 0 ? 0x80000000 >> (block - 1) : 0;
    }

    /**
     * @param ip e.g. 0xF0000000
     * @return e.g. 4
     */
    private static long calcMaxBlock(long ip) {
        long maxsize = 32;
        while (maxsize > 0) {
            long mask = getMaskByBlock(maxsize - 1);
            long maskedBase = ip & mask;
            if (maskedBase != ip) {
                break;
            }
            maxsize--;
        }
        return maxsize;
    }

    /**
     * @param cidrNotation A CIDR-notation string, e.g. "192.168.0.0/16"
     * @param value        A value
     */
    public void insert(String cidrNotation, V value) {
        if (value == null)
            throw new IllegalArgumentException("Value cannot be null");

        int pos = cidrNotation.indexOf('/');

        if (pos == -1) {
            insert(cidrNotation + "/255", value);
            return;
        }

        String ipStr = cidrNotation.substring(0, pos);
        int ip = Utils.ipAddrToInt(ipStr);
        int cidr = Integer.parseInt(cidrNotation.substring(pos + 1));

        acquireWriteLock();
        tree.insert(ip, getMaskByBlock(cidr), value);
        releaseWriteLock();
    }


    /**
     * @param dotDecimalRangeStart A dot-decimal notation range start string, e.g. "192.168.0.0"
     * @param dotDecimalRangeEnd   A dot-decimal notation range end string, e.g. "192.168.255.255"
     * @param value                A value
     */
    public void insert(String dotDecimalRangeStart, String dotDecimalRangeEnd, V value) {
        if (value == null)
            throw new IllegalArgumentException("Value cannot be null");

        long start = Utils.ipAddrToInt(dotDecimalRangeStart) & 0xFFFFFFFFL;
        long end = Utils.ipAddrToInt(dotDecimalRangeEnd) & 0xFFFFFFFFL;

        while (end >= start) {
            long maxBlock = calcMaxBlock(start);
            long maxDiff = (int) (32 - Math.floor(Math.log(end - start + 1) / Math.log(2)));
            maxBlock = maxBlock > maxDiff ? maxBlock : maxDiff;
            acquireWriteLock();
            tree.insert((int) start, getMaskByBlock(maxBlock), value);
            releaseWriteLock();
            start += Math.pow(2, (32 - maxBlock));
        }
    }

    /**
     * @param dotDecimalNotation A quad-dotted notation string, e.g. "192.168.5.25"
     * @return A Value
     */
    public V find(String dotDecimalNotation) {
        V value = tree.find(Utils.ipAddrToInt(dotDecimalNotation));
        return value == null ? defaultValue : value;
    }
}
