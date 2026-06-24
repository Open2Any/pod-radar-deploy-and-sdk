package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;
import java.util.OptionalInt;

/**
 * Response of {@code POST /api/v1/fangguo/runs}. Two shapes:
 * <ul>
 *   <li>A single started run → {@code {run_id, status}} ({@code status} always {@code "running"});
 *       {@link #isQueuedAll()} is {@code false}.</li>
 *   <li>An "all accounts" trigger (incremental, no window, no {@code account_id}, with accounts present)
 *       → {@code {queued, scope:"all"}}: {@link #isQueuedAll()} is {@code true}, {@link #queued()} holds the
 *       number of accounts queued, {@link #runId()} is {@code 0}.</li>
 * </ul>
 * A 409 with an already-running run id is surfaced as a thrown {@code PodRadarConflictException}.
 */
public final class FangguoRunResponse {
    private final long runId;
    private final String status;
    private final String scope;
    private final OptionalInt queued;

    public FangguoRunResponse(long runId, String status, String scope, OptionalInt queued) {
        this.runId = runId;
        this.status = status;
        this.scope = scope;
        this.queued = queued;
    }

    /** The started run's id, or {@code 0} when {@link #isQueuedAll()}. */
    public long runId()         { return runId; }
    public String status()      { return status; }
    /** {@code "all"} when this trigger queued every enabled account; otherwise {@code null}. */
    public String scope()       { return scope; }
    /** Number of accounts queued when {@link #isQueuedAll()}; empty for a single-run response. */
    public OptionalInt queued() { return queued; }
    /** True when the server queued a next round for all enabled accounts instead of starting one run. */
    public boolean isQueuedAll() { return "all".equals(scope); }

    public static FangguoRunResponse fromJson(Map<String, Object> o) {
        return new FangguoRunResponse(
                Json.lng(o, "run_id"),
                Json.str(o, "status"),
                Json.str(o, "scope"),
                Json.optInt(o, "queued"));
    }
}
