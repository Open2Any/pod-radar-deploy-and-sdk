package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/**
 * Result of {@code POST /api/v1/accounts/test}. A failed upstream login is NOT an HTTP error —
 * it comes back as {@code ok=false} with the upstream failure reason in {@link #error()}.
 */
public final class AccountTestResult {
    private final boolean ok;
    private final String error;

    public AccountTestResult(boolean ok, String error) {
        this.ok = ok;
        this.error = error;
    }

    /** {@code true} if the upstream login succeeded. */
    public boolean ok()      { return ok; }

    /** Upstream failure reason when {@link #ok()} is {@code false}, else {@code null}. */
    public String error()    { return error; }

    public static AccountTestResult fromJson(Map<String, Object> o) {
        Object v = o.get("ok");
        boolean ok = v instanceof Boolean ? (Boolean) v : false;
        return new AccountTestResult(ok, Json.str(o, "error"));
    }
}
