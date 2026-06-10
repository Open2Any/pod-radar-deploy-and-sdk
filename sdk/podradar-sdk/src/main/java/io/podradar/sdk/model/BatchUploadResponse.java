package io.podradar.sdk.model;

import io.podradar.sdk.internal.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Response of {@code POST /api/v1/images/batch} or {@code .../retry-failed}. */
public final class BatchUploadResponse {
    private final String jobId;
    private final int total;
    private final int succeeded;
    private final int failed;
    private final int duplicate;
    private final List<Item> results;

    private BatchUploadResponse(String jobId, int total, int succeeded, int failed, int duplicate, List<Item> results) {
        this.jobId = jobId;
        this.total = total;
        this.succeeded = succeeded;
        this.failed = failed;
        this.duplicate = duplicate;
        this.results = results;
    }

    public String jobId() { return jobId; }
    public int total() { return total; }
    public int succeeded() { return succeeded; }
    public int failed() { return failed; }
    public int duplicate() { return duplicate; }
    public List<Item> results() { return results; }

    public static BatchUploadResponse fromJson(Map<String, Object> o) {
        List<Item> items = new ArrayList<>();
        for (Object raw : Json.list(o, "results")) {
            items.add(Item.fromJson(Json.asMap(raw)));
        }
        return new BatchUploadResponse(
                Json.str(o, "job_id"),
                Json.integ(o, "total"),
                Json.integ(o, "succeeded"),
                Json.integ(o, "failed"),
                Json.integ(o, "duplicate"),
                Collections.unmodifiableList(items));
    }

    public static final class Item {
        private final String imageUrl;
        private final Long imageId;
        private final boolean created;
        private final boolean duplicate;
        private final String error;

        private Item(String imageUrl, Long imageId, boolean created, boolean duplicate, String error) {
            this.imageUrl = imageUrl;
            this.imageId = imageId;
            this.created = created;
            this.duplicate = duplicate;
            this.error = error;
        }

        public String imageUrl() { return imageUrl; }
        public Long imageId() { return imageId; }
        public boolean created() { return created; }
        public boolean duplicate() { return duplicate; }
        public String error() { return error; }

        static Item fromJson(Map<String, Object> o) {
            return new Item(
                    Json.str(o, "image_url"),
                    o.get("image_id") instanceof Number ? ((Number) o.get("image_id")).longValue() : null,
                    Json.bool(o, "created"),
                    Json.bool(o, "duplicate"),
                    Json.str(o, "error"));
        }
    }
}
