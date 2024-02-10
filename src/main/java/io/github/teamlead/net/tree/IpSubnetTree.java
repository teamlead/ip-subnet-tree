package io.github.teamlead.net.tree;

import io.github.teamlead.net.Utils;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Represents a data structure for efficient IP subnet storage and lookup. This class
 * utilizes a radix tree (trie) for storing values associated with IP subnets. It allows
 * for quick retrieval of values based on IP address inputs, supporting both CIDR notation
 * and range insertion for subnet definitions. It is designed with concurrency in mind,
 * employing a {@link java.util.concurrent.locks.ReadWriteLock} to manage concurrent access.
 *
 * @param <V> the type of value that the IP subnets will be associated with in this tree
 */
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

    /**
     * Constructs a new IPSubnetTree. Initializes the internal data structures required
     * for IP subnet storage and retrieval.
     */
    public IpSubnetTree() {
        tree = new RadixInt32Tree<V>();
    }

    /**
     * Returns the default value to be used when no specific value is found for an IP address lookup.
     *
     * @return the default value
     */
    public V getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value to be returned when no specific value is found for an IP address lookup.
     *
     * @param value the default value to set
     */
    public void setDefaultValue(V value) {
        defaultValue = value;
    }

    /**
     * @param block eg. 24
     * @return eg. 255.255.255.0
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
     * Inserts a value associated with a subnet defined in CIDR notation into the IP subnet tree.
     *
     * @param cidrNotation A CIDR-notation string, e.g., "192.168.0.0/16", representing the IP subnet
     * @param value        The value to associate with the specified IP subnet
     */
    public void insert(String cidrNotation, V value) {
        if (value == null)
            throw new IllegalArgumentException("Value cannot be null");

        int pos = cidrNotation.indexOf('/');

        if (pos == -1) {
            insert(cidrNotation + "/32", value);
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
     * Inserts a value associated with an IP range specified in dot-decimal notation.
     *
     * @param dotDecimalRangeStart A dot-decimal notation string representing the start of the IP range, e.g., "192.168.0.0"
     * @param dotDecimalRangeEnd   A dot-decimal notation string representing the end of the IP range, e.g., "192.168.255.255"
     * @param value                The value to associate with the specified IP range
     */
    public void insert(String dotDecimalRangeStart, String dotDecimalRangeEnd, V value) {
        if (value == null)
            throw new IllegalArgumentException("Value cannot be null");

        long start = Utils.ipAddrToInt(dotDecimalRangeStart) & 0xFFFFFFFFL;
        long end = Utils.ipAddrToInt(dotDecimalRangeEnd) & 0xFFFFFFFFL;

        while (end >= start) {
            long maxBlock = calcMaxBlock(start);
            long maxDiff = (int) (32 - Math.floor(Math.log(end - start + 1) / Math.log(2)));
            maxBlock = Math.max(maxBlock, maxDiff);
            acquireWriteLock();
            tree.insert((int) start, getMaskByBlock(maxBlock), value);
            releaseWriteLock();
            start += Math.pow(2, (32 - maxBlock));
        }
    }

    /**
     * Finds and returns the value associated with a specific IP address.
     *
     * @param dotDecimalNotation A quad-dotted notation string, e.g., "192.168.5.25", representing the IP address
     * @return The value associated with the specified IP address or the default value if no association exists
     */
    public V find(String dotDecimalNotation) {
        V value = tree.find(Utils.ipAddrToInt(dotDecimalNotation));
        return value == null ? defaultValue : value;
    }
}
