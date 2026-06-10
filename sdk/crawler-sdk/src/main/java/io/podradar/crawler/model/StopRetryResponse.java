package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/**
 * Response of {@code POST /api/v1/hihumbird/runs/{runId}/stop-retry} — stops an in-progress
 * enqueue-type retry batch (signals harvest to stop, flushes the batch's still-pending queue).
 * A missing run yields HTTP 404 (a thrown exception), so this DTO only models the success shape.
 */
public final class StopRetryResponse {
    private final String status;
    private final long runId;
    private final int stoppedAssets;
    private final int stoppedLabels;

    public StopRetryResponse(String status, long runId, int stoppedAssets, int stoppedLabels) {
        this.status = status;
        this.runId = runId;
        this.stoppedAssets = stoppedAssets;
        this.stoppedLabels = stoppedLabels;
    }

    public String status()      { return status; }
    public long runId()         { return runId; }
    public int stoppedAssets()  { return stoppedAssets; }
    public int stoppedLabels()  { return stoppedLabels; }

    public static StopRetryResponse fromJson(Map<String, Object> o) {
        return new StopRetryResponse(
                Json.str(o, "status"),
                Json.lng(o, "run_id"),
                Json.integ(o, "stopped_assets"),
                Json.integ(o, "stopped_labels"));
    }
}
