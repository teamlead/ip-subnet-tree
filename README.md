IpSubnetTree
============

An implementation of an IP radix trie for CIDR lookups. The current version supports only IPv4 addresses.

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

A realistic example using the Spring Framework and a CSV file in the format of [ngx_http_geo_module](http://nginx.org/en/docs/http/ngx_http_geo_module.html):

```java
import io.github.teamlead.net.tree.IpSubnetTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class GeoIpService {

    private final IpSubnetTree<String> tree = new IpSubnetTree<>();
    private final ResourceLoader resourceLoader;
    private final String dbPath;

    public GeoIpService(ResourceLoader resourceLoader,
                        @Value("${geoip.db.path:classpath:path/to/db.csv}") String dbPath) {
        this.resourceLoader = resourceLoader;
        this.dbPath = dbPath;
    }

    @PostConstruct
    public void initGeoIpDatabase() throws IOException {
        Resource resource = resourceLoader.getResource(dbPath);
        loadDatabase(resource);
    }

    private void loadDatabase(Resource resource) throws IOException {
        if (!resource.isReadable()) {
            throw new IOException("Cannot read resource from " + resource.getDescription());
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            reader.lines()
                    .filter(line -> !StringUtils.isEmpty(line))
                    .forEach(this::processLine);
        }
    }

    private void processLine(String line) {
        String[] columns = line.split("\t", -1);
        if (columns.length != 2) {
            throw new IllegalArgumentException("Invalid line format: " + line);
        }

        String ipRange = columns[0].trim();
        String geoCode = columns[1].replaceFirst(";$", "").trim();
        tree.insert(ipRange, geoCode);
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
    <groupId>io.github.teamlead</groupId>
    <artifactId>ip-subnet-tree</artifactId>
    <version>1.1.0</version>
</dependency>
```

Gradle/Grails dependency:

```
compile 'io.github.teamlead:ip-subnet-tree:1.1.0'
```

## Tests
```
$ mvn test
```

## License
MIT
