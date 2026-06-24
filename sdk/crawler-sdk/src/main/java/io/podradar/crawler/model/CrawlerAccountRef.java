package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/**
 * Lightweight reference to an upstream account, embedded in a run's {@code accounts[]} list — the
 * set of accounts a sync run actually covered (a run sweeps every enabled account of its system).
 * Distinct from {@link CrawlerAccount}, which is the full management record.
 */
public final class CrawlerAccountRef {
    private final long id;
    private final String name;
    private final String username;

    public CrawlerAccountRef(long id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

    public long id()         { return id; }
    public String name()     { return name; }
    public String username() { return username; }

    public static CrawlerAccountRef fromJson(Map<String, Object> o) {
        return new CrawlerAccountRef(Json.lng(o, "id"), Json.str(o, "name"), Json.str(o, "username"));
    }
}
