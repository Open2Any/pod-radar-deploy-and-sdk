package io.podradar.sdk.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/** {@code 202} response from {@code POST /api/v1/search/batch}. */
public final class BatchSearchAsyncResponse {
    private final long searchJobId;
    private final int total;
    private final int enqueued;
    private final String status;
    private final String statusUrl;

    private BatchSearchAsyncResponse(long searchJobId, int total, int enqueued, String status, String statusUrl) {
        this.searchJobId = searchJobId;
        this.total = total;
        this.enqueued = enqueued;
        this.status = status;
        this.statusUrl = statusUrl;
    }

    public long searchJobId() { return searchJobId; }
    public int total() { return total; }
    public int enqueued() { return enqueued; }
    public String status() { return status; }
    public String statusUrl() { return statusUrl; }

    public static BatchSearchAsyncResponse fromJson(Map<String, Object> o) {
        return new BatchSearchAsyncResponse(
                Json.lng(o, "search_job_id"),
                Json.integ(o, "total"),
                Json.integ(o, "enqueued"),
                Json.str(o, "status"),
                Json.str(o, "status_url"));
    }
}
