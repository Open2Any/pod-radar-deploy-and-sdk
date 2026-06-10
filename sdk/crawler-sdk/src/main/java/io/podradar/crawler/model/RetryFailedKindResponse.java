package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * Response of {@code POST /api/v1/hihumbird/retry-failed}.
 *
 * <ul>
 *   <li>{@code status="queued"} → one or more retry batches were created (status='done'
 *       enqueue-type runs). For {@code product_image} with harvest enabled the failed images
 *       are auto-chunked by 1000 items into multiple harvest runs ({@link #batches()} /
 *       {@link #runIds()}); they wait for the headless-browser re-render before being fetched
 *       ({@link #reharvest()} is true).</li>
 *   <li>{@code status="empty"} → nothing failed under the filter; no batch created.</li>
 * </ul>
 */
public final class RetryFailedKindResponse {
    private final String status;
    private final OptionalLong runId;
    private final String kind;
    private final int queued;
    private final int skippedNoUrl;
    private final boolean reharvest;
    private final OptionalInt batches;
    private final List<Long> runIds;

    public RetryFailedKindResponse(String status, OptionalLong runId, String kind, int queued,
                                   int skippedNoUrl, boolean reharvest, OptionalInt batches,
                                   List<Long> runIds) {
        this.status = status;
        this.runId = runId;
        this.kind = kind;
        this.queued = queued;
        this.skippedNoUrl = skippedNoUrl;
        this.reharvest = reharvest;
        this.batches = batches;
        this.runIds = runIds;
    }

    public String status()        { return status; }
    public boolean isQueued()     { return "queued".equals(status); }
    public boolean isEmpty()      { return "empty".equals(status); }
    /** First/representative batch run id; empty when {@code status="empty"}. */
    public OptionalLong runId()   { return runId; }
    public String kind()          { return kind; }
    /** Number of asset/label rows re-enqueued (or handed to harvest). */
    public int queued()           { return queued; }
    /** Failed product images with no URL — only recoverable via harvest re-render. */
    public int skippedNoUrl()     { return skippedNoUrl; }
    /** True when product_image retry triggered headless-browser re-render. */
    public boolean reharvest()    { return reharvest; }
    /** Number of harvest batches when product_image was auto-chunked (≤1000 items each). */
    public OptionalInt batches()  { return batches; }
    /** Run ids of the chunked product_image batches; empty list otherwise. */
    public List<Long> runIds()    { return runIds; }

    public static RetryFailedKindResponse fromJson(Map<String, Object> o) {
        List<Long> ids = new ArrayList<>();
        for (Object raw : Json.list(o, "run_ids")) {
            if (raw instanceof Number) ids.add(((Number) raw).longValue());
        }
        return new RetryFailedKindResponse(
                Json.str(o, "status"),
                Json.optLong(o, "run_id"),
                Json.str(o, "kind"),
                Json.integ(o, "queued"),
                Json.integ(o, "skipped_no_url"),
                Json.bool(o, "reharvest"),
                Json.optInt(o, "batches"),
                Collections.unmodifiableList(ids));
    }
}
