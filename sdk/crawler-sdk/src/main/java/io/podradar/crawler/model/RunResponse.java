package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;
import java.util.OptionalInt;

/**
 * Response of {@code POST /runs} (and {@code POST /runs/{id}/retry-failed} via cast).
 *
 * <p>Two shapes:
 * <ul>
 *   <li>A single started run → {@code {run_id, status}}: {@link #runId()} / {@link #status()} are set,
 *       {@link #isQueuedAll()} is {@code false}.</li>
 *   <li>An "all accounts" trigger (incremental, no window, no {@code account_id}, with accounts present)
 *       → {@code {queued, scope:"all"}}: {@link #isQueuedAll()} is {@code true}, {@link #queued()} holds the
 *       number of accounts queued, {@link #runId()} is {@code 0} (the scheduler starts them one-by-one).</li>
 * </ul>
 */
public final class RunResponse {
    private final long runId;
    private final String status;
    private final OptionalInt itemCount;
    private final String scope;
    private final OptionalInt queued;

    public RunResponse(long runId, String status, OptionalInt itemCount, String scope, OptionalInt queued) {
        this.runId = runId;
        this.status = status;
        this.itemCount = itemCount;
        this.scope = scope;
        this.queued = queued;
    }

    /** The started run's id, or {@code 0} when {@link #isQueuedAll()} (no single run was created). */
    public long runId()              { return runId; }
    public String status()           { return status; }
    public OptionalInt itemCount()   { return itemCount; }
    /** {@code "all"} when this trigger queued every enabled account; otherwise {@code null}. */
    public String scope()            { return scope; }
    /** Number of accounts queued when {@link #isQueuedAll()}; empty for a single-run response. */
    public OptionalInt queued()      { return queued; }
    /** True when the server queued a next round for all enabled accounts instead of starting one run. */
    public boolean isQueuedAll()     { return "all".equals(scope); }

    public static RunResponse fromJson(Map<String, Object> o) {
        return new RunResponse(
                Json.lng(o, "run_id"),
                Json.str(o, "status"),
                Json.optInt(o, "item_count"),
                Json.str(o, "scope"),
                Json.optInt(o, "queued"));
    }
}
