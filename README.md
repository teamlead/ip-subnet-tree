IpSubnetTree
============

[![Build Status](http://img.shields.io/travis/x25/ip-subnet-tree/master.svg?style=flat-square)](https://travis-ci.org/x25/ip-subnet-tree)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.x25/ip-subnet-tree/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.github.x25/ip-subnet-tree/)

An implementation of IP radix trie for CIDR Lookups. Current version supports only IPv4 addresses.

## Usage:

Basic usage:

```java
IpSubnetTree<String> tree = new IpSubnetTree<String>();
tree.setDefaultValue("Unknown");

// CIDR notation:
tree.insert("8.8.8.0/24", "Google");
// Range:
tree.insert("127.0.0.0",  "127.255.255.255", "localhost");
// Single IP:
tree.insert("77.219.59.9", "WAP Tele2");

assertEquals("Google",    tree.find("8.8.8.8"));
assertEquals("localhost", tree.find("127.0.0.1"));
assertEquals("WAP Tele2", tree.find("77.219.59.9"));
assertEquals("Unknown",   tree.find("10.0.0.1"));
```

A realistic example using the Spring Framework and a csv file in [ngx_http_geo_module](http://nginx.org/en/docs/http/ngx_http_geo_module.html) format:

```java
import com.github.x25.tree.IpSubnetTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import javax.annotation.PostConstruct;
import java.io.*;

@Service
public class GeoIpService {

    private final IpSubnetTree<String> tree = new IpSubnetTree<String>();

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void postConstruct() throws IOException {

        // Nginx geo module config format, e.g.:
        // 127.0.0.1      foo;
        // 192.168.1.0/24 bar;
        // 10.1.0.0/16    baz;
        Resource resource = resourceLoader.getResource("classpath:" + "path/to/db.csv");

        if (!resource.isReadable()) {
            throw new RuntimeException("Cannot read file");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8"));
        String line;
        
        while ((line = br.readLine()) != null) {
        
            if (StringUtils.isEmpty(line)) {
                continue;
            }

            String[] columns = line.split("\t", -1);

            if (columns.length != 2) {
                throw new RuntimeException("Invalid line: " + line);
            }

            tree.insert(columns[0].trim(), columns[1].replaceFirst(";$", "").trim());
        }

        br.close();
    }

    public String getGeoCodeByIp(String ip) {
        return tree.find(ip);
    }
}
```

## Installation:

This library is available on Maven Central.

Apache Maven dependency:

```xml
<dependency>
    <groupId>com.github.x25</groupId>
    <artifactId>ip-subnet-tree</artifactId>
    <version>1.0.2</version>
</dependency>
```

Gradle/Grails dependency:

```
compile 'com.github.x25:ip-subnet-tree:1.0.2'
```

## Tests
```
$ mvn test
```

## License
MIT
