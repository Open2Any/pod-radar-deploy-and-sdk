package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.LinkedHashMap;
import java.util.Map;

/** The 8-field hihumbird sync settings block (shared by {@code GET}/{@code PUT /settings}). */
public final class HihumbirdSettings {
    private final boolean syncEnabled;
    private final int syncIntervalMinutes;
    private final int syncOverlapMinutes;
    private final String cursorStartAt;
    private final int maxRunSpanHours;
    private final boolean rescanPendingEnabled;
    private final int rescanPendingIntervalMinutes;
    private final int rescanPendingMaxAgeDays;

    public HihumbirdSettings(boolean syncEnabled, int syncIntervalMinutes, int syncOverlapMinutes,
                             String cursorStartAt, int maxRunSpanHours, boolean rescanPendingEnabled,
                             int rescanPendingIntervalMinutes, int rescanPendingMaxAgeDays) {
        this.syncEnabled = syncEnabled;
        this.syncIntervalMinutes = syncIntervalMinutes;
        this.syncOverlapMinutes = syncOverlapMinutes;
        this.cursorStartAt = cursorStartAt;
        this.maxRunSpanHours = maxRunSpanHours;
        this.rescanPendingEnabled = rescanPendingEnabled;
        this.rescanPendingIntervalMinutes = rescanPendingIntervalMinutes;
        this.rescanPendingMaxAgeDays = rescanPendingMaxAgeDays;
    }

    public boolean syncEnabled() { return syncEnabled; }
    public int syncIntervalMinutes() { return syncIntervalMinutes; }
    public int syncOverlapMinutes() { return syncOverlapMinutes; }
    public String cursorStartAt() { return cursorStartAt; }
    public int maxRunSpanHours() { return maxRunSpanHours; }
    public boolean rescanPendingEnabled() { return rescanPendingEnabled; }
    public int rescanPendingIntervalMinutes() { return rescanPendingIntervalMinutes; }
    public int rescanPendingMaxAgeDays() { return rescanPendingMaxAgeDays; }

    public static HihumbirdSettings fromJson(Map<String, Object> o) {
        return new HihumbirdSettings(
                Json.bool(o, "sync_enabled"),
                Json.integ(o, "sync_interval_minutes"),
                Json.integ(o, "sync_overlap_minutes"),
                Json.str(o, "cursor_start_at"),
                Json.integ(o, "max_run_span_hours"),
                Json.bool(o, "rescan_pending_enabled"),
                Json.integ(o, "rescan_pending_interval_minutes"),
                Json.integ(o, "rescan_pending_max_age_days"));
    }

    public Map<String, Object> toJson() {
        Map<String, Object> o = new LinkedHashMap<>();
        o.put("sync_enabled", syncEnabled);
        o.put("sync_interval_minutes", syncIntervalMinutes);
        o.put("sync_overlap_minutes", syncOverlapMinutes);
        o.put("cursor_start_at", cursorStartAt);
        o.put("max_run_span_hours", maxRunSpanHours);
        o.put("rescan_pending_enabled", rescanPendingEnabled);
        o.put("rescan_pending_interval_minutes", rescanPendingIntervalMinutes);
        o.put("rescan_pending_max_age_days", rescanPendingMaxAgeDays);
        return o;
    }
}
