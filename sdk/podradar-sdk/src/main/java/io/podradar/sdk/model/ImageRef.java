package io.podradar.sdk.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/** Single search hit. Returned inside {@link SearchResponse#results()} and in batch-search items. */
public final class ImageRef {
    private final long imageId;
    private final double score;
    private final String source;
    private final String sourceId;
    private final String url;
    private final String title;
    private final String thumb;
    private final String full;
    private final String status;

    private ImageRef(Builder b) {
        this.imageId = b.imageId;
        this.score = b.score;
        this.source = b.source;
        this.sourceId = b.sourceId;
        this.url = b.url;
        this.title = b.title;
        this.thumb = b.thumb;
        this.full = b.full;
        this.status = b.status;
    }

    public long imageId() { return imageId; }
    public double score() { return score; }
    public String source() { return source; }
    public String sourceId() { return sourceId; }
    public String url() { return url; }
    public String title() { return title; }
    public String thumb() { return thumb; }
    public String full() { return full; }
    public String status() { return status; }

    public static ImageRef fromJson(Map<String, Object> o) {
        return new Builder()
                .imageId(Json.lng(o, "id"))
                .score(Json.dbl(o, "score"))
                .source(Json.str(o, "source"))
                .sourceId(Json.str(o, "source_id"))
                .url(Json.str(o, "url"))
                .title(Json.str(o, "title"))
                .thumb(Json.str(o, "thumb"))
                .full(Json.str(o, "full"))
                .status(Json.str(o, "status"))
                .build();
    }

    static final class Builder {
        long imageId;
        double score;
        String source, sourceId, url, title, thumb, full, status;
        Builder imageId(long v) { this.imageId = v; return this; }
        Builder score(double v) { this.score = v; return this; }
        Builder source(String v) { this.source = v; return this; }
        Builder sourceId(String v) { this.sourceId = v; return this; }
        Builder url(String v) { this.url = v; return this; }
        Builder title(String v) { this.title = v; return this; }
        Builder thumb(String v) { this.thumb = v; return this; }
        Builder full(String v) { this.full = v; return this; }
        Builder status(String v) { this.status = v; return this; }
        ImageRef build() { return new ImageRef(this); }
    }
}
