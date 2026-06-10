package io.podradar.crawler.model;

import java.util.LinkedHashMap;
import java.util.Map;

/** Builder for {@code POST /api/v1/hihumbird/runs}. */
public final class RunRequest {

    public enum Mode { INCREMENTAL, BACKFILL }

    private final Mode mode;
    private final Long from;
    private final Long to;
    private String batchCode;
    private boolean dryRun;

    private RunRequest(Mode mode, Long from, Long to) {
        this.mode = mode;
        this.from = from;
        this.to = to;
    }

    /** Resume from the server-side cursor. */
    public static RunRequest incremental() {
        return new RunRequest(Mode.INCREMENTAL, null, null);
    }

    /** Backfill a closed time window (epoch milliseconds, {@code from < to}). */
    public static RunRequest backfill(long fromMs, long toMs) {
        if (fromMs >= toMs) throw new IllegalArgumentException("from must be < to");
        return new RunRequest(Mode.BACKFILL, fromMs, toMs);
    }

    public RunRequest withBatchCode(String code) { this.batchCode = code; return this; }
    public RunRequest withDryRun(boolean v)      { this.dryRun = v; return this; }

    public Mode mode()        { return mode; }
    public Long from()        { return from; }
    public Long to()          { return to; }
    public String batchCode() { return batchCode; }
    public boolean dryRun()   { return dryRun; }

    /** JSON body shape as sent to the server. */
    public Map<String, Object> toJson() {
        Map<String, Object> o = new LinkedHashMap<>();
        o.put("mode", mode == Mode.BACKFILL ? "backfill" : "incremental");
        if (from != null) o.put("from", from);
        if (to != null) o.put("to", to);
        if (batchCode != null) o.put("batch_code", batchCode);
        if (dryRun) o.put("dry_run", true);
        return o;
    }
}
