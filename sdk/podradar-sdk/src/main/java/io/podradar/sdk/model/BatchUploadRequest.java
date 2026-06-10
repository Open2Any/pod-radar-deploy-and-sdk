package io.podradar.sdk.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Builder for {@code POST /api/v1/images/batch}. URL-only, ≤50 per batch. */
public final class BatchUploadRequest {
    private final List<URI> urls;
    private String source;
    private String sourceId;
    private String title;

    private BatchUploadRequest(List<URI> urls) {
        this.urls = urls;
    }

    public static BatchUploadRequest fromUrls(List<URI> urls) {
        Objects.requireNonNull(urls, "urls");
        if (urls.isEmpty()) throw new IllegalArgumentException("urls is empty");
        if (urls.size() > 50) throw new IllegalArgumentException("urls > 50");
        for (URI u : urls) {
            if (u == null) throw new IllegalArgumentException("null url in batch");
        }
        return new BatchUploadRequest(Collections.unmodifiableList(new ArrayList<>(urls)));
    }

    public BatchUploadRequest withSource(String source) { this.source = source; return this; }
    public BatchUploadRequest withSourceId(String sourceId) { this.sourceId = sourceId; return this; }
    public BatchUploadRequest withTitle(String title) { this.title = title; return this; }

    public List<URI> urls() { return urls; }
    public String source() { return source; }
    public String sourceId() { return sourceId; }
    public String title() { return title; }
}
