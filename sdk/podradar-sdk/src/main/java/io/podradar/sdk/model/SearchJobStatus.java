package io.podradar.sdk.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/** Response of {@code GET /api/v1/search/jobs/{id}}. */
public final class SearchJobStatus {
    private final long searchJobId;
    private final String status;
    private final String model;
    private final int k;
    private final double minScore;
    private final int total;
    private final int completed;
    private final int failed;
    private final String startedAt;
    private final String finishedAt;

    private SearchJobStatus(long searchJobId, String status, String model, int k, double minScore,
                            int total, int completed, int failed, String startedAt, String finishedAt) {
        this.searchJobId = searchJobId;
        this.status = status;
        this.model = model;
        this.k = k;
        this.minScore = minScore;
        this.total = total;
        this.completed = completed;
        this.failed = failed;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public long searchJobId() { return searchJobId; }
    public String status() { return status; }
    public String model() { return model; }
    public int k() { return k; }
    public double minScore() { return minScore; }
    public int total() { return total; }
    public int completed() { return completed; }
    public int failed() { return failed; }
    public String startedAt() { return startedAt; }
    public String finishedAt() { return finishedAt; }

    public static SearchJobStatus fromJson(Map<String, Object> o) {
        Map<String, Object> counts = Json.obj(o, "counts");
        return new SearchJobStatus(
                Json.lng(o, "search_job_id"),
                Json.str(o, "status"),
                Json.str(o, "model"),
                Json.integ(o, "k"),
                Json.dbl(o, "min_score"),
                Json.integ(counts, "total"),
                Json.integ(counts, "completed"),
                Json.integ(counts, "failed"),
                Json.str(o, "started_at"),
                Json.str(o, "finished_at"));
    }
}
