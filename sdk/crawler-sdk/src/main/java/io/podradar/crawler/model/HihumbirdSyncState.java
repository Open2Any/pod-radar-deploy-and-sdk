package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/** Cursor snapshot returned in {@code GET /settings} alongside the settings block. */
public final class HihumbirdSyncState {
    private final String lastSuccessAt;
    private final String lastStartedAt;
    private final Long lastRunId;
    private final Long lastSuccessCreatedFrom;
    private final Long lastSuccessCreatedTo;

    public HihumbirdSyncState(String lastSuccessAt, String lastStartedAt, Long lastRunId,
                              Long lastSuccessCreatedFrom, Long lastSuccessCreatedTo) {
        this.lastSuccessAt = lastSuccessAt;
        this.lastStartedAt = lastStartedAt;
        this.lastRunId = lastRunId;
        this.lastSuccessCreatedFrom = lastSuccessCreatedFrom;
        this.lastSuccessCreatedTo = lastSuccessCreatedTo;
    }

    public String lastSuccessAt() { return lastSuccessAt; }
    public String lastStartedAt() { return lastStartedAt; }
    public Long lastRunId() { return lastRunId; }
    public Long lastSuccessCreatedFrom() { return lastSuccessCreatedFrom; }
    public Long lastSuccessCreatedTo() { return lastSuccessCreatedTo; }

    public static HihumbirdSyncState fromJson(Map<String, Object> o) {
        if (o == null || o.isEmpty()) return new HihumbirdSyncState(null, null, null, null, null);
        Long from = null, to = null;
        Map<String, Object> range = Json.obj(o, "last_success_created_range");
        if (range != null && !range.isEmpty()) {
            from = nullableLong(range.get("from"));
            to = nullableLong(range.get("to"));
        }
        return new HihumbirdSyncState(
                Json.str(o, "last_success_at"),
                Json.str(o, "last_started_at"),
                nullableLong(o.get("last_run_id")),
                from,
                to);
    }

    private static Long nullableLong(Object v) {
        return v instanceof Number ? ((Number) v).longValue() : null;
    }
}
