package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

/** Response of {@code GET /runs/{id}/items} and {@code GET /items}. */
public final class ItemsListResponse {
    private final List<HihumbirdItem> items;
    private final int total;
    private final int limit;
    private final int offset;
    private final Long runId;
    private final OptionalInt historyOrderDays;

    public ItemsListResponse(List<HihumbirdItem> items, int total, int limit, int offset, Long runId,
                             OptionalInt historyOrderDays) {
        this.items = items;
        this.total = total;
        this.limit = limit;
        this.offset = offset;
        this.runId = runId;
        this.historyOrderDays = historyOrderDays;
    }

    public List<HihumbirdItem> items() { return items; }
    public int total()                 { return total; }
    public int limit()                 { return limit; }
    public int offset()                { return offset; }
    /** Present on {@code /runs/{id}/items} responses, {@code null} on cross-run {@code /items}. */
    public Long runId()                { return runId; }
    /**
     * History-order window in days (server's {@code CRAWLER_HISTORY_ORDER_DAYS}, default 90):
     * the "retry failed" actions only retry orders created within this many days; older orders
     * are skipped. Present on cross-run {@code /items}, empty on {@code /runs/{id}/items}.
     */
    public OptionalInt historyOrderDays() { return historyOrderDays; }

    public static ItemsListResponse fromJson(Map<String, Object> o) {
        List<HihumbirdItem> items = new ArrayList<>();
        for (Object raw : Json.list(o, "items")) {
            items.add(HihumbirdItem.fromJson(Json.asMap(raw)));
        }
        Long runId = o.get("run_id") instanceof Number ? ((Number) o.get("run_id")).longValue() : null;
        return new ItemsListResponse(
                Collections.unmodifiableList(items),
                Json.integ(o, "total"),
                Json.integ(o, "limit"),
                Json.integ(o, "offset"),
                runId,
                Json.optInt(o, "history_order_days"));
    }
}
