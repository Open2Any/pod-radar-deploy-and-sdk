package io.podradar.sdk.model;

import io.podradar.sdk.internal.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Response of {@code GET /api/v1/search/jobs/{id}/items}. */
public final class SearchJobItemsResponse {
    private final int total;
    private final int limit;
    private final int offset;
    private final List<Item> items;

    private SearchJobItemsResponse(int total, int limit, int offset, List<Item> items) {
        this.total = total;
        this.limit = limit;
        this.offset = offset;
        this.items = items;
    }

    public int total() { return total; }
    public int limit() { return limit; }
    public int offset() { return offset; }
    public List<Item> items() { return items; }

    public static SearchJobItemsResponse fromJson(Map<String, Object> o) {
        List<Item> items = new ArrayList<>();
        for (Object raw : Json.list(o, "items")) {
            items.add(Item.fromJson(Json.asMap(raw)));
        }
        return new SearchJobItemsResponse(
                Json.integ(o, "total"),
                Json.integ(o, "limit"),
                Json.integ(o, "offset"),
                Collections.unmodifiableList(items));
    }

    /** A single URL's search result row. */
    public static final class Item {
        private final long id;
        private final int itemIndex;
        private final String imageUrl;
        private final String status;
        private final int attempts;
        private final String lastError;
        private final List<ImageRef> hits;

        private Item(long id, int itemIndex, String imageUrl, String status, int attempts,
                     String lastError, List<ImageRef> hits) {
            this.id = id;
            this.itemIndex = itemIndex;
            this.imageUrl = imageUrl;
            this.status = status;
            this.attempts = attempts;
            this.lastError = lastError;
            this.hits = hits;
        }

        public long id() { return id; }
        public int itemIndex() { return itemIndex; }
        public String imageUrl() { return imageUrl; }
        public String status() { return status; }
        public int attempts() { return attempts; }
        public String lastError() { return lastError; }
        /** May be {@code null} if the worker hasn't processed this item yet. Empty list = no match. */
        public List<ImageRef> hits() { return hits; }

        static Item fromJson(Map<String, Object> o) {
            List<ImageRef> hits = null;
            Object results = o.get("results");
            if (results instanceof Map) {
                Map<String, Object> r = Json.asMap(results);
                List<ImageRef> hh = new ArrayList<>();
                for (Object it : Json.list(r, "items")) {
                    hh.add(ImageRef.fromJson(Json.asMap(it)));
                }
                hits = Collections.unmodifiableList(hh);
            }
            return new Item(
                    Json.lng(o, "id"),
                    Json.integ(o, "item_index"),
                    Json.str(o, "image_url"),
                    Json.str(o, "status"),
                    Json.integ(o, "attempts"),
                    Json.str(o, "last_error"),
                    hits);
        }
    }
}
