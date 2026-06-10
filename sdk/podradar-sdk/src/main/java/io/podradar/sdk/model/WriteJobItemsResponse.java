package io.podradar.sdk.model;

import io.podradar.sdk.internal.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Response of {@code GET /api/v1/images/jobs/{job_id}/items}. */
public final class WriteJobItemsResponse {
    private final int total;
    private final int limit;
    private final int offset;
    private final List<Item> items;

    private WriteJobItemsResponse(int total, int limit, int offset, List<Item> items) {
        this.total = total;
        this.limit = limit;
        this.offset = offset;
        this.items = items;
    }

    public int total() { return total; }
    public int limit() { return limit; }
    public int offset() { return offset; }
    public List<Item> items() { return items; }

    public static WriteJobItemsResponse fromJson(Map<String, Object> o) {
        List<Item> items = new ArrayList<>();
        for (Object raw : Json.list(o, "items")) {
            items.add(Item.fromJson(Json.asMap(raw)));
        }
        return new WriteJobItemsResponse(
                Json.integ(o, "total"),
                Json.integ(o, "limit"),
                Json.integ(o, "offset"),
                Collections.unmodifiableList(items));
    }

    public static final class Item {
        private final long id;
        private final String jobId;
        private final int itemIndex;
        private final String uploadMode;
        private final String imageUrl;
        private final String status;
        private final int attempts;
        private final String lastError;
        private final Long imageId;
        private final String createdAt;
        private final String updatedAt;

        private Item(long id, String jobId, int itemIndex, String uploadMode, String imageUrl,
                     String status, int attempts, String lastError, Long imageId,
                     String createdAt, String updatedAt) {
            this.id = id;
            this.jobId = jobId;
            this.itemIndex = itemIndex;
            this.uploadMode = uploadMode;
            this.imageUrl = imageUrl;
            this.status = status;
            this.attempts = attempts;
            this.lastError = lastError;
            this.imageId = imageId;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public long id() { return id; }
        public String jobId() { return jobId; }
        public int itemIndex() { return itemIndex; }
        public String uploadMode() { return uploadMode; }
        public String imageUrl() { return imageUrl; }
        public String status() { return status; }
        public int attempts() { return attempts; }
        public String lastError() { return lastError; }
        public Long imageId() { return imageId; }
        public String createdAt() { return createdAt; }
        public String updatedAt() { return updatedAt; }

        static Item fromJson(Map<String, Object> o) {
            return new Item(
                    Json.lng(o, "id"),
                    Json.str(o, "job_id"),
                    Json.integ(o, "item_index"),
                    Json.str(o, "upload_mode"),
                    Json.str(o, "image_url"),
                    Json.str(o, "status"),
                    Json.integ(o, "attempts"),
                    Json.str(o, "last_error"),
                    o.get("image_id") instanceof Number ? ((Number) o.get("image_id")).longValue() : null,
                    Json.str(o, "created_at"),
                    Json.str(o, "updated_at"));
        }
    }
}
