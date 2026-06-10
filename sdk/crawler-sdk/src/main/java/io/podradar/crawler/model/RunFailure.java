package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/** A single (error, count) row inside {@link RunSummary#failures()}. */
public final class RunFailure {
    private final String error;
    private final int count;

    public RunFailure(String error, int count) {
        this.error = error;
        this.count = count;
    }

    public String error() { return error; }
    public int count()    { return count; }

    public static RunFailure fromJson(Map<String, Object> o) {
        return new RunFailure(Json.str(o, "error"), Json.integ(o, "count"));
    }
}
