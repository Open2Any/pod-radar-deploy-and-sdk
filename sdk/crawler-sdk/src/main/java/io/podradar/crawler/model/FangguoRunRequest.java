package io.podradar.crawler.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builder for {@code POST /api/v1/fangguo/runs}. Unlike hihumbird there is no
 * {@code batch_code}: a fangguo run is identified solely by its {@code run_id}.
 */
public final class FangguoRunRequest {

    public enum Mode { INCREMENTAL, BACKFILL }

    private final Mode mode;
    private final Long from;
    private final Long to;
    private boolean dryRun;
    private Long accountId;

    private FangguoRunRequest(Mode mode, Long from, Long to) {
        this.mode = mode;
        this.from = from;
        this.to = to;
    }

    /** Resume from the server-side cursor. */
    public static FangguoRunRequest incremental() {
        return new FangguoRunRequest(Mode.INCREMENTAL, null, null);
    }

    /**
     * Backfill a closed time window (epoch milliseconds, {@code from < to}). The server clamps the
     * window to at most 31 days — the cover/task upstream rejects anything wider.
     */
    public static FangguoRunRequest backfill(long fromMs, long toMs) {
        if (fromMs >= toMs) throw new IllegalArgumentException("from must be < to");
        return new FangguoRunRequest(Mode.BACKFILL, fromMs, toMs);
    }

    public FangguoRunRequest withDryRun(boolean v) { this.dryRun = v; return this; }
    /**
     * Scope this run to a single upstream account (its own cursor/settings; the batch is attributed
     * to it). Omit to target the global default; with accounts present, an incremental run with no
     * window then queues a next round for ALL enabled accounts (response carries {@code scope=all}).
     */
    public FangguoRunRequest withAccountId(long id) { this.accountId = id; return this; }

    public Mode mode()      { return mode; }
    public Long from()      { return from; }
    public Long to()        { return to; }
    public boolean dryRun() { return dryRun; }
    public Long accountId() { return accountId; }

    /** JSON body shape as sent to the server. */
    public Map<String, Object> toJson() {
        Map<String, Object> o = new LinkedHashMap<>();
        o.put("mode", mode == Mode.BACKFILL ? "backfill" : "incremental");
        if (from != null) o.put("from", from);
        if (to != null) o.put("to", to);
        if (dryRun) o.put("dry_run", true);
        if (accountId != null) o.put("account_id", accountId);
        return o;
    }
}
