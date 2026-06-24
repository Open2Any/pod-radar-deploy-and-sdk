package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Row in {@code GET /runs}. {@code id} and {@code run_id} are the same; expose {@code id()}. */
public final class RunSummary {
    private final long id;
    private final String trigger;
    private final String mode;
    private final Long windowFrom;
    private final Long windowTo;
    private final String status;
    private final String error;
    private final String startedAt;
    private final String finishedAt;
    private final Map<String, Object> jobParams;
    private final int queued;
    private final int fetched;
    private final int failed;
    private final int duplicate;
    private final Map<String, Object> counts;
    private final List<RunFailure> failures;
    private final String system;
    private final List<CrawlerAccountRef> accounts;

    public RunSummary(long id, String trigger, String mode, Long windowFrom, Long windowTo,
                      String status, String error, String startedAt, String finishedAt,
                      Map<String, Object> jobParams, int queued, int fetched, int failed,
                      int duplicate, Map<String, Object> counts, List<RunFailure> failures,
                      String system, List<CrawlerAccountRef> accounts) {
        this.id = id;
        this.trigger = trigger;
        this.mode = mode;
        this.windowFrom = windowFrom;
        this.windowTo = windowTo;
        this.status = status;
        this.error = error;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.jobParams = jobParams;
        this.queued = queued;
        this.fetched = fetched;
        this.failed = failed;
        this.duplicate = duplicate;
        this.counts = counts;
        this.failures = failures;
        this.system = system;
        this.accounts = accounts;
    }

    public long id()             { return id; }
    public String trigger()      { return trigger; }
    public String mode()         { return mode; }
    public Long windowFrom()     { return windowFrom; }
    public Long windowTo()       { return windowTo; }
    public String status()       { return status; }
    public String error()        { return error; }
    public String startedAt()    { return startedAt; }
    public String finishedAt()   { return finishedAt; }
    public Map<String, Object> jobParams() { return jobParams; }
    public int queued()          { return queued; }
    public int fetched()         { return fetched; }
    public int failed()          { return failed; }
    public int duplicate()       { return duplicate; }
    public Map<String, Object> counts() { return counts; }
    public List<RunFailure> failures() { return failures; }
    /** Always {@code "hihumbird"}. */
    public String system()       { return system; }
    /** Upstream accounts this run covered (a run sweeps every enabled account of its system). */
    public List<CrawlerAccountRef> accounts() { return accounts; }

    public static RunSummary fromJson(Map<String, Object> o) {
        List<RunFailure> failures = new ArrayList<>();
        for (Object raw : Json.list(o, "failures")) {
            failures.add(RunFailure.fromJson(Json.asMap(raw)));
        }
        List<CrawlerAccountRef> accounts = new ArrayList<>();
        for (Object raw : Json.list(o, "accounts")) {
            accounts.add(CrawlerAccountRef.fromJson(Json.asMap(raw)));
        }
        return new RunSummary(
                Json.lng(o, "id"),
                Json.str(o, "trigger"),
                Json.str(o, "mode"),
                nullableLong(o.get("window_from")),
                nullableLong(o.get("window_to")),
                Json.str(o, "status"),
                Json.str(o, "error"),
                Json.str(o, "started_at"),
                Json.str(o, "finished_at"),
                Json.obj(o, "job_params"),
                Json.integ(o, "queued"),
                Json.integ(o, "fetched"),
                Json.integ(o, "failed"),
                Json.integ(o, "duplicate"),
                Json.obj(o, "counts"),
                Collections.unmodifiableList(failures),
                Json.str(o, "system"),
                Collections.unmodifiableList(accounts));
    }

    private static Long nullableLong(Object v) {
        return v instanceof Number ? ((Number) v).longValue() : null;
    }
}
