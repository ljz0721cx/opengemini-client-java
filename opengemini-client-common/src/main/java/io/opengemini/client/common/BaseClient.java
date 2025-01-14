package io.opengemini.client.common;

import io.opengemini.client.api.Address;
import io.opengemini.client.api.BaseConfiguration;
import io.opengemini.client.api.Query;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BaseClient {
    private final List<String> serverUrls = new ArrayList<>();

    private final AtomicInteger prevIndex = new AtomicInteger(-1);

    public BaseClient(BaseConfiguration conf) {
        String httpPrefix;
        if (conf.isTlsEnabled()) {
            httpPrefix = "https://";
        } else {
            httpPrefix = "http://";
        }
        for (Address address : conf.getAddresses()) {
            this.serverUrls.add(httpPrefix + address.getHost() + ":" + address.getPort());
        }
    }

    protected String nextUrlPrefix() {
        int idx = Math.abs(prevIndex.incrementAndGet() % serverUrls.size());
        return serverUrls.get(idx);
    }

    protected String encode(String str) {
        try {
            return URLEncoder.encode(str, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            throw new IllegalStateException("utf-8 should be supported by jvm", e);
        }
    }

    protected String getWriteUrl(String database) {
        String writeUrl = UrlConst.WRITE + "?db=" + database;
        return writeUrl;
    }

    protected String getQueryUrl(Query query) {
        String queryUrl = UrlConst.QUERY + "?q=" + encode(query.getCommand());

        if (query.getDatabase() != null) {
            queryUrl += "&db=" + query.getDatabase();
        }

        if (query.getRetentionPolicy() != null) {
            queryUrl += "&rp=" + query.getRetentionPolicy();
        }
        return queryUrl;
    }
}
