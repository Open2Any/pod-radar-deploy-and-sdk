package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/** Response of {@code GET /api/v1/hihumbird/settings}: {@code {settings, state}}. */
public final class SettingsResponse {
    private final HihumbirdSettings settings;
    private final HihumbirdSyncState state;

    public SettingsResponse(HihumbirdSettings settings, HihumbirdSyncState state) {
        this.settings = settings;
        this.state = state;
    }

    public HihumbirdSettings settings() { return settings; }
    public HihumbirdSyncState state() { return state; }

    public static SettingsResponse fromJson(Map<String, Object> o) {
        return new SettingsResponse(
                HihumbirdSettings.fromJson(Json.obj(o, "settings")),
                HihumbirdSyncState.fromJson(Json.obj(o, "state")));
    }
}
