package io.podradar.sdk.model;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Builder for {@code POST /api/v1/search/batch}. ≤500 URLs per batch. */
public final class BatchSearchRequest {
    private final List<URI> urls;
    private int k = 24;
    private double minScore = 0.0;

    private BatchSearchRequest(List<URI> urls) {
        this.urls = urls;
    }

    public static BatchSearchRequest fromUrls(List<URI> urls) {
        Objects.requireNonNull(urls, "urls");
        if (urls.isEmpty()) throw new IllegalArgumentException("urls is empty");
        if (urls.size() > 500) throw new IllegalArgumentException("urls > 500");
        for (URI u : urls) {
            if (u == null) throw new IllegalArgumentException("null url in batch");
        }
        return new BatchSearchRequest(Collections.unmodifiableList(new java.util.ArrayList<>(urls)));
    }

    public BatchSearchRequest withK(int k) {
        if (k < 1 || k > 100) throw new IllegalArgumentException("k must be in [1, 100]");
        this.k = k;
        return this;
    }

    public BatchSearchRequest withMinScore(double m) {
        if (m < 0.0 || m > 1.0) throw new IllegalArgumentException("minScore must be in [0, 1]");
        this.minScore = m;
        return this;
    }

    public List<URI> urls() { return urls; }
    public int k() { return k; }
    public double minScore() { return minScore; }
}
