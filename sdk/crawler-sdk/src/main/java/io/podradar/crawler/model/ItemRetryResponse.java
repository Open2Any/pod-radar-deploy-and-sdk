package io.podradar.crawler.model;

import java.util.Collections;
import java.util.Map;

/**
 * Loose wrapper around {@code POST /api/v1/hihumbird/items/{id}/retry}: the server
 * response shape is not yet frozen, so we expose the raw JSON map alongside a few
 * common fields.
 */
public final class ItemRetryResponse {
    private final Map<String, Object> raw;

    public ItemRetryResponse(Map<String, Object> raw) {
        this.raw = raw == null ? Collections.emptyMap() : Collections.unmodifiableMap(raw);
    }

    /** Server-reported outcome status (e.g. {@code "queued"}, {@code "skipped"}). */
    public String status() {
        Object v = raw.get("status");
        return v == null ? null : String.valueOf(v);
    }

    /** Echoed item id, when present. */
    public Long itemId() {
        Object v = raw.get("item_id");
        return v instanceof Number ? ((Number) v).longValue() : null;
    }

    /** The full untyped JSON object as returned. */
    public Map<String, Object> raw() { return raw; }
}
