package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/**
 * Response of {@code POST /api/v1/keys}. {@link #plaintext()} is the full token returned
 * <b>exactly once</b>; the server does not store it. Save it before the JVM exits.
 */
public final class CreateKeyResponse {
    private final long id;
    private final String name;
    private final String prefix;
    private final String createdAt;
    private final String lastUsedAt;
    private final long useCount;
    private final String revokedAt;
    private final String plaintext;

    public CreateKeyResponse(long id, String name, String prefix, String createdAt,
                             String lastUsedAt, long useCount, String revokedAt, String plaintext) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.createdAt = createdAt;
        this.lastUsedAt = lastUsedAt;
        this.useCount = useCount;
        this.revokedAt = revokedAt;
        this.plaintext = plaintext;
    }

    public long id()             { return id; }
    public String name()         { return name; }
    public String prefix()       { return prefix; }
    public String createdAt()    { return createdAt; }
    public String lastUsedAt()   { return lastUsedAt; }
    public long useCount()       { return useCount; }
    public String revokedAt()    { return revokedAt; }
    public String plaintext()    { return plaintext; }

    public static CreateKeyResponse fromJson(Map<String, Object> o) {
        return new CreateKeyResponse(
                Json.lng(o, "id"),
                Json.str(o, "name"),
                Json.str(o, "prefix"),
                Json.str(o, "created_at"),
                Json.str(o, "last_used_at"),
                Json.lng(o, "use_count"),
                Json.str(o, "revoked_at"),
                Json.str(o, "plaintext"));
    }
}
