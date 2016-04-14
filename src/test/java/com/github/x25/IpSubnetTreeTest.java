package com.github.x25;

import com.github.x25.net.tree.IpSubnetTree;
import com.github.x25.net.Utils;
import junit.framework.TestCase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IpSubnetTreeTest extends TestCase {

    public void testExample()
    {
        IpSubnetTree<String> tree = new IpSubnetTree<String>();
        tree.setDefaultValue("Unknown");

        tree.insert("8.8.8.0/24", "Google");
        tree.insert("127.0.0.0", "127.255.255.255", "localhost");
        tree.insert("77.219.59.9", "WAP Tele2");

        assertEquals("Google", tree.find("8.8.8.8"));
        assertEquals("localhost", tree.find("127.0.0.1"));
        assertEquals("WAP Tele2", tree.find("77.219.59.9"));
        assertEquals("Unknown", tree.find("10.0.0.1"));
    }

    public void testBasic() {
        IpSubnetTree<String> tree = new IpSubnetTree<String>();
        tree.setDefaultValue("X");
        tree.insert("127.0.0.6", "127.0.0.6", "D");
        tree.insert("127.0.0.0", "127.0.0.255", "B");
        tree.insert("127.0.0.7", "E");
        tree.insert("127.0.0.0/8", "A");
        tree.insert("127.0.0.5/255", "C");

        assertEquals("X", tree.getDefaultValue());

        assertEquals("A", tree.find("127.1.2.3"));
        assertEquals("B", tree.find("127.0.0.1"));
        assertEquals("C", tree.find("127.0.0.5"));
        assertEquals("D", tree.find("127.0.0.6"));
        assertEquals("E", tree.find("127.0.0.7"));
        assertEquals("X", tree.find("126.0.0.1"));
        assertEquals("X", tree.find("128.0.0.1"));
    }

    public void testCidr() {
        IpSubnetTree<Integer> tree = new IpSubnetTree<Integer>();
        for (int i = 0; i < 255; i++) {
            tree.insert(i + ".0.0.0/9", i);
        }
        for (int i = 0; i < 255; i++) {
            assertEquals(i, (int) tree.find(i + ".127.0.255"));
            assertEquals(null, tree.find(i + ".128.0.255"));
        }
    }

    public void testRange() {
        IpSubnetTree<Integer> tree = new IpSubnetTree<Integer>();
        for (int i = 0; i < 255; i++) {
            tree.insert(i + ".0.0.0", i + ".127.255.255", i);
        }
        for (int i = 0; i < 255; i++) {
            assertEquals(i, (int) tree.find(i + ".127.0.255"));
            assertEquals(null, tree.find(i + ".128.0.255"));
        }
    }

    public void testOverride() {
        IpSubnetTree<String> test = new IpSubnetTree<String>();

        test.insert("1.2.3.4/255", "foo");
        assertEquals("foo", test.find("1.2.3.4"));

        test.insert("1.2.3.4/255", "bar");
        assertEquals("bar", test.find("1.2.3.4"));
    }

    public void testDotAll() {
        IpSubnetTree<String> foo = new IpSubnetTree<String>();

        foo.insert("0.0.0.0/0", "foo");
        assertEquals("foo", foo.find("1.2.3.4"));
        assertEquals("foo", foo.find("0.0.0.0"));
        assertEquals("foo", foo.find("255.255.255.255"));

        IpSubnetTree<String> bar = new IpSubnetTree<String>();
        bar.insert("0.0.0.0", "255.255.255.255", "bar");
        assertEquals("bar", bar.find("1.2.3.4"));
        assertEquals("bar", bar.find("0.0.0.0"));
        assertEquals("bar", bar.find("255.255.255.255"));
    }

    public void testIllegalArgument() {
        IpSubnetTree<Boolean> foo = new IpSubnetTree<Boolean>();
        try {
            foo.insert("abc", true);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    public void testUtils() {
        assertEquals("255.128.127.1", Utils.intToIpAddr(Utils.ipAddrToInt("255.128.127.1")));
        assertEquals("255.255.255.255", Utils.intToIpAddr(Utils.ipAddrToInt("255.255.255.255")));
        assertEquals("1.127.128.255", Utils.intToIpAddr(Utils.ipAddrToInt("1.127.128.255")));
        assertEquals("0.0.0.0", Utils.intToIpAddr(Utils.ipAddrToInt("0.0.0.0")));
    }
}
