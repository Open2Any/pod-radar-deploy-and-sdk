package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;
import java.util.OptionalInt;

/** Response of {@code POST /runs} (and {@code POST /runs/{id}/retry-failed} via cast). */
public final class RunResponse {
    private final long runId;
    private final String status;
    private final OptionalInt itemCount;

    public RunResponse(long runId, String status, OptionalInt itemCount) {
        this.runId = runId;
        this.status = status;
        this.itemCount = itemCount;
    }

    public long runId()              { return runId; }
    public String status()           { return status; }
    public OptionalInt itemCount()   { return itemCount; }

    public static RunResponse fromJson(Map<String, Object> o) {
        return new RunResponse(
                Json.lng(o, "run_id"),
                Json.str(o, "status"),
                Json.optInt(o, "item_count"));
    }
}
