package io.podradar.sdk.model;

import io.podradar.sdk.internal.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Response of {@code POST /api/v1/search}. */
public final class SearchResponse {
    private final int k;
    private final double minScore;
    private final String model;
    private final List<ImageRef> results;

    private SearchResponse(int k, double minScore, String model, List<ImageRef> results) {
        this.k = k;
        this.minScore = minScore;
        this.model = model;
        this.results = results;
    }

    public int k() { return k; }
    public double minScore() { return minScore; }
    public String model() { return model; }
    public List<ImageRef> results() { return results; }

    public static SearchResponse fromJson(Map<String, Object> o) {
        List<ImageRef> hits = new ArrayList<>();
        for (Object item : Json.list(o, "results")) {
            hits.add(ImageRef.fromJson(Json.asMap(item)));
        }
        return new SearchResponse(
                Json.integ(o, "k"),
                Json.dbl(o, "min_score"),
                Json.str(o, "model"),
                Collections.unmodifiableList(hits));
    }
}
