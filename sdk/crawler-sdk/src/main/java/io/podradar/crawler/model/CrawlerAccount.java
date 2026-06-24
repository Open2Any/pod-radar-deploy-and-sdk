package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/**
 * A row from {@code GET /api/v1/accounts} — an upstream login account (one of the crawler
 * systems). Passwords / secrets are NEVER returned by the API; only the username is shown.
 */
public final class CrawlerAccount {
    private final long id;
    private final String system; // "hihumbird" | "fangguo"
    private final String name;
    private final String username;
    private final String baseUrl;
    private final String portalUrl;
    private final Map<String, Object> params;
    private final String syncCursor;
    private final String syncNextAt;
    private final boolean enabled;
    private final String lastSuccessAt;
    private final String createdAt;
    private final String updatedAt;
    private final String revokedAt;

    public CrawlerAccount(long id, String system, String name, String username, String baseUrl,
                          String portalUrl, Map<String, Object> params,
                          String syncCursor, String syncNextAt, boolean enabled,
                          String lastSuccessAt, String createdAt, String updatedAt, String revokedAt) {
        this.id = id;
        this.system = system;
        this.name = name;
        this.username = username;
        this.baseUrl = baseUrl;
        this.portalUrl = portalUrl;
        this.params = params;
        this.syncCursor = syncCursor;
        this.syncNextAt = syncNextAt;
        this.enabled = enabled;
        this.lastSuccessAt = lastSuccessAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.revokedAt = revokedAt;
    }

    public long id()            { return id; }
    public String system()      { return system; }
    public String name()        { return name; }
    public String username()    { return username; }
    /** The account's crawl gateway URL, or {@code null} (uses the system default). */
    public String baseUrl()     { return baseUrl; }
    /** The account's portal/site domain — display label only, NOT used for crawling; may be {@code null}. */
    public String portalUrl()   { return portalUrl; }
    /** System-specific non-secret params (hihumbird: group_id/app_id/rel_type/scene_id; fangguo: tenant_id). Never {@code null}. */
    public Map<String, Object> params() { return params; }
    /** Per-account incremental cursor (last order-created time synced), or {@code null} if never run. */
    public String syncCursor()  { return syncCursor; }
    /** Next scheduled run time for this account, or {@code null} (due now). */
    public String syncNextAt()  { return syncNextAt; }
    public boolean enabled()    { return enabled; }
    public String lastSuccessAt() { return lastSuccessAt; }
    public String createdAt()   { return createdAt; }
    public String updatedAt()   { return updatedAt; }
    public String revokedAt()   { return revokedAt; }

    public static CrawlerAccount fromJson(Map<String, Object> o) {
        return new CrawlerAccount(
                Json.lng(o, "id"),
                Json.str(o, "system"),
                Json.str(o, "name"),
                Json.str(o, "username"),
                Json.str(o, "base_url"),
                Json.str(o, "portal_url"),
                Json.obj(o, "params"),
                Json.str(o, "sync_cursor"),
                Json.str(o, "sync_next_at"),
                Json.bool(o, "enabled"),
                Json.str(o, "last_success_at"),
                Json.str(o, "created_at"),
                Json.str(o, "updated_at"),
                Json.str(o, "revoked_at"));
    }
}
