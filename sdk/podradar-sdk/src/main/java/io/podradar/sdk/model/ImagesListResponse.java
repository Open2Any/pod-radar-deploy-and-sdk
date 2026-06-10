package io.podradar.sdk.model;

import io.podradar.sdk.internal.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Response of {@code GET /api/v1/images}. */
public final class ImagesListResponse {
    private final List<ImageDto> images;
    private final int total;
    private final int canonicalTotal;
    private final int duplicateTotal;
    private final boolean includeDuplicates;
    private final int limit;
    private final int offset;

    private ImagesListResponse(List<ImageDto> images, int total, int canonicalTotal, int duplicateTotal,
                               boolean includeDuplicates, int limit, int offset) {
        this.images = images;
        this.total = total;
        this.canonicalTotal = canonicalTotal;
        this.duplicateTotal = duplicateTotal;
        this.includeDuplicates = includeDuplicates;
        this.limit = limit;
        this.offset = offset;
    }

    public List<ImageDto> images() { return images; }
    public int total() { return total; }
    public int canonicalTotal() { return canonicalTotal; }
    public int duplicateTotal() { return duplicateTotal; }
    public boolean includeDuplicates() { return includeDuplicates; }
    public int limit() { return limit; }
    public int offset() { return offset; }

    public static ImagesListResponse fromJson(Map<String, Object> o) {
        List<ImageDto> imgs = new ArrayList<>();
        for (Object raw : Json.list(o, "images")) {
            imgs.add(ImageDto.fromJson(Json.asMap(raw)));
        }
        return new ImagesListResponse(
                Collections.unmodifiableList(imgs),
                Json.integ(o, "total"),
                Json.integ(o, "canonical_total"),
                Json.integ(o, "duplicate_total"),
                Json.bool(o, "include_duplicates"),
                Json.integ(o, "limit"),
                Json.integ(o, "offset"));
    }
}
