package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/** A row from {@code GET /api/v1/keys}. {@code plaintext} only exposed on create — see {@link CreateKeyResponse}. */
public final class CrawlerKey {
    private final long id;
    private final String name;
    private final String prefix;
    private final String createdAt;
    private final String lastUsedAt;
    private final long useCount;
    private final String revokedAt;

    public CrawlerKey(long id, String name, String prefix, String createdAt,
                      String lastUsedAt, long useCount, String revokedAt) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.createdAt = createdAt;
        this.lastUsedAt = lastUsedAt;
        this.useCount = useCount;
        this.revokedAt = revokedAt;
    }

    public long id()             { return id; }
    public String name()         { return name; }
    public String prefix()       { return prefix; }
    public String createdAt()    { return createdAt; }
    public String lastUsedAt()   { return lastUsedAt; }
    public long useCount()       { return useCount; }
    public String revokedAt()    { return revokedAt; }

    public static CrawlerKey fromJson(Map<String, Object> o) {
        return new CrawlerKey(
                Json.lng(o, "id"),
                Json.str(o, "name"),
                Json.str(o, "prefix"),
                Json.str(o, "created_at"),
                Json.str(o, "last_used_at"),
                Json.lng(o, "use_count"),
                Json.str(o, "revoked_at"));
    }
}
