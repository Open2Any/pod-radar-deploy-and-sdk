package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;
import java.util.OptionalInt;

/** Response of {@code POST /api/v1/hihumbird/runs/{id}/retry-failed}. */
public final class RetryRunResponse {
    private final long runId;
    private final String status;
    private final OptionalInt itemCount;

    public RetryRunResponse(long runId, String status, OptionalInt itemCount) {
        this.runId = runId;
        this.status = status;
        this.itemCount = itemCount;
    }

    public long runId()              { return runId; }
    public String status()           { return status; }
    public OptionalInt itemCount()   { return itemCount; }

    public static RetryRunResponse fromJson(Map<String, Object> o) {
        return new RetryRunResponse(
                Json.lng(o, "run_id"),
                Json.str(o, "status"),
                Json.optInt(o, "item_count"));
    }
}
