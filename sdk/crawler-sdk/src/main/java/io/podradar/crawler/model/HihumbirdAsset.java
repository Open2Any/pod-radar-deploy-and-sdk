package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/**
 * One hihumbird image asset (product/production/source) attached to an order item. {@code assetKind}
 * is {@code product_image} | {@code production_image} | {@code source_image}. {@code thumb} and
 * {@code full} point at the same sha-addressed crawler-bucket URL (no separate thumbnail) and are
 * {@code null} until the asset reaches {@code fetched}; {@code id}/{@code width}/{@code height} are
 * {@code null} before the bytes are harvested.
 */
public final class HihumbirdAsset {
    private final Long id;
    private final String assetKind;
    private final String status;
    private final String externalKey;
    private final String url;
    private final String lastError;
    private final String title;
    private final Integer width;
    private final Integer height;
    private final String thumb;
    private final String full;

    public HihumbirdAsset(Long id, String assetKind, String status, String externalKey, String url,
                          String lastError, String title, Integer width, Integer height,
                          String thumb, String full) {
        this.id = id;
        this.assetKind = assetKind;
        this.status = status;
        this.externalKey = externalKey;
        this.url = url;
        this.lastError = lastError;
        this.title = title;
        this.width = width;
        this.height = height;
        this.thumb = thumb;
        this.full = full;
    }

    public Long id()            { return id; }
    public String assetKind()   { return assetKind; }
    public String status()      { return status; }
    public String externalKey() { return externalKey; }
    public String url()         { return url; }
    public String lastError()   { return lastError; }
    public String title()       { return title; }
    public Integer width()      { return width; }
    public Integer height()     { return height; }
    public String thumb()       { return thumb; }
    public String full()        { return full; }

    public static HihumbirdAsset fromJson(Map<String, Object> o) {
        return new HihumbirdAsset(
                nullableLong(o.get("id")),
                Json.str(o, "asset_kind"),
                Json.str(o, "status"),
                Json.str(o, "external_key"),
                Json.str(o, "url"),
                Json.str(o, "last_error"),
                Json.str(o, "title"),
                nullableInt(o.get("width")),
                nullableInt(o.get("height")),
                Json.str(o, "thumb"),
                Json.str(o, "full"));
    }

    private static Long nullableLong(Object v) {
        return v instanceof Number ? ((Number) v).longValue() : null;
    }

    private static Integer nullableInt(Object v) {
        return v instanceof Number ? ((Number) v).intValue() : null;
    }
}
