package io.podradar.crawler.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Body for {@code PATCH /api/v1/accounts/{id}} — all fields optional; only the ones set are
 * changed. Setting {@code password} re-encrypts the secret; system-specific fields are merged
 * onto the existing secret. At least one field must be set (server returns 400 otherwise).
 */
public final class UpdateAccountRequest {
    private String name;
    private String username;
    private String password;
    private Boolean enabled;
    private String baseUrl;
    private String portalUrl;
    private String tenantId;
    private Long groupId;
    private Long appId;
    private Long relType;
    private String sceneId;

    public static UpdateAccountRequest empty() { return new UpdateAccountRequest(); }

    public UpdateAccountRequest withName(String v)     { this.name = v; return this; }
    public UpdateAccountRequest withUsername(String v) { this.username = v; return this; }
    public UpdateAccountRequest withPassword(String v) { this.password = v; return this; }
    public UpdateAccountRequest withEnabled(boolean v) { this.enabled = v; return this; }
    /** Set the crawl gateway URL; pass {@code ""} to clear it back to the system default. */
    public UpdateAccountRequest withBaseUrl(String v)  { this.baseUrl = v; return this; }
    /** Portal/site domain (display label only); pass {@code ""} to clear. */
    public UpdateAccountRequest withPortalUrl(String v){ this.portalUrl = v; return this; }
    public UpdateAccountRequest withTenantId(String v) { this.tenantId = v; return this; }
    public UpdateAccountRequest withGroupId(long v)    { this.groupId = v; return this; }
    public UpdateAccountRequest withAppId(long v)      { this.appId = v; return this; }
    public UpdateAccountRequest withRelType(long v)    { this.relType = v; return this; }
    public UpdateAccountRequest withSceneId(String v)  { this.sceneId = v; return this; }

    public Map<String, Object> toJson() {
        Map<String, Object> o = new LinkedHashMap<>();
        if (name != null) o.put("name", name);
        if (username != null) o.put("username", username);
        if (password != null) o.put("password", password);
        if (enabled != null) o.put("enabled", enabled);
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
