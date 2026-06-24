package io.podradar.crawler.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Body for {@code POST /api/v1/accounts/test} — verify an upstream login without saving.
 *
 * <p>Two modes: {@link #forAccount(String, long)} tests a stored account's credentials (the
 * stored account's real system is authoritative server-side; any system passed here is ignored
 * when {@code accountId} is set), optionally overriding fields to test edits before saving;
 * {@link #withCredentials(String, String, String)} tests raw not-yet-saved credentials.
 */
public final class TestAccountRequest {
    private final String system; // "hihumbird" | "fangguo"
    private Long accountId;
    private String username;
    private String password;
    private String baseUrl;
    private String tenantId;
    private Long groupId;
    private Long appId;
    private Long relType;
    private String sceneId;

    private TestAccountRequest(String system) {
        this.system = system;
    }

    /** Test a stored account's credentials by id. */
    public static TestAccountRequest forAccount(String system, long accountId) {
        TestAccountRequest r = new TestAccountRequest(system);
        r.accountId = accountId;
        return r;
    }

    /** Test raw credentials not yet persisted (add-form flow). */
    public static TestAccountRequest withCredentials(String system, String username, String password) {
        TestAccountRequest r = new TestAccountRequest(system);
        r.username = username;
        r.password = password;
        return r;
    }

    public TestAccountRequest withUsername(String v) { this.username = v; return this; }
    public TestAccountRequest withPassword(String v) { this.password = v; return this; }
    public TestAccountRequest withBaseUrl(String v)  { this.baseUrl = v; return this; }
    public TestAccountRequest withTenantId(String v) { this.tenantId = v; return this; }
    public TestAccountRequest withGroupId(long v)    { this.groupId = v; return this; }
    public TestAccountRequest withAppId(long v)      { this.appId = v; return this; }
    public TestAccountRequest withRelType(long v)    { this.relType = v; return this; }
    public TestAccountRequest withSceneId(String v)  { this.sceneId = v; return this; }

    public Map<String, Object> toJson() {
        Map<String, Object> o = new LinkedHashMap<>();
        o.put("system", system);
        if (accountId != null) o.put("account_id", accountId);
        if (username != null) o.put("username", username);
        if (password != null) o.put("password", password);
        if (baseUrl != null) o.put("base_url", baseUrl);
        if (tenantId != null) o.put("tenant_id", tenantId);
        if (groupId != null) o.put("group_id", groupId);
        if (appId != null) o.put("app_id", appId);
        if (relType != null) o.put("rel_type", relType);
        if (sceneId != null) o.put("scene_id", sceneId);
        return o;
    }
}
