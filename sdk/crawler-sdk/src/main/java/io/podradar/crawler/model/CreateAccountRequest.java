package io.podradar.crawler.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Body for {@code POST /api/v1/accounts} — register an upstream login account.
 * System-specific fields are optional (default to server {@code .env} / config):
 * fangguo uses {@code tenantId}; hihumbird uses {@code groupId}/{@code appId}/{@code relType}/{@code sceneId}.
 * The password is write-only — the API never returns it.
 */
public final class CreateAccountRequest {
    private final String system; // "hihumbird" | "fangguo"
    private final String name;
    private final String username;
    private final String password;
    private String baseUrl;
    private String portalUrl;
    private String tenantId;
    private Long groupId;
    private Long appId;
    private Long relType;
    private String sceneId;

    public CreateAccountRequest(String system, String name, String username, String password) {
        this.system = system;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    /** Override the crawl gateway URL (e.g. a different deployment of the same system). */
    public CreateAccountRequest withBaseUrl(String v)  { this.baseUrl = v; return this; }
    /** Portal/site domain — display label only, not used for crawling (e.g. www.jxpod.com). */
    public CreateAccountRequest withPortalUrl(String v){ this.portalUrl = v; return this; }
    public CreateAccountRequest withTenantId(String v) { this.tenantId = v; return this; }
    public CreateAccountRequest withGroupId(long v)    { this.groupId = v; return this; }
    public CreateAccountRequest withAppId(long v)      { this.appId = v; return this; }
    public CreateAccountRequest withRelType(long v)    { this.relType = v; return this; }
    public CreateAccountRequest withSceneId(String v)  { this.sceneId = v; return this; }

    public Map<String, Object> toJson() {
        Map<String, Object> o = new LinkedHashMap<>();
        o.put("system", system);
        o.put("name", name);
        o.put("username", username);
        o.put("password", password);
        if (baseUrl != null) o.put("base_url", baseUrl);
        if (portalUrl != null) o.put("portal_url", portalUrl);
        if (tenantId != null) o.put("tenant_id", tenantId);
        if (groupId != null) o.put("group_id", groupId);
        if (appId != null) o.put("app_id", appId);
        if (relType != null) o.put("rel_type", relType);
        if (sceneId != null) o.put("scene_id", sceneId);
        return o;
    }
}
