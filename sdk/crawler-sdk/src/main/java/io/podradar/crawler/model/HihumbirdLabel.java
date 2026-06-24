package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.List;
import java.util.Map;

/**
 * The {@code label} sub-block on a {@link HihumbirdItem} (the order's shipping label), or {@code null}
 * when the order has no label. {@code shippingUrl}/{@code thumbnailUrl} are presigned upstream URLs;
 * {@code pdfUrl} is the rendered-PDF crawler-bucket URL. Fields are {@code null} until fetched.
 */
public final class HihumbirdLabel {
    private final String status;
    private final List<String> trackNumbers;
    private final String logisticsMethod;
    private final String shippingUrl;
    private final String thumbnailUrl;
    private final String failedUrl;
    private final String pdfUrl;
    private final String pdfObjectKey;
    private final Integer pageCount;
    private final String lastError;

    public HihumbirdLabel(String status, List<String> trackNumbers, String logisticsMethod,
                          String shippingUrl, String thumbnailUrl, String failedUrl, String pdfUrl,
                          String pdfObjectKey, Integer pageCount, String lastError) {
        this.status = status;
        this.trackNumbers = trackNumbers;
        this.logisticsMethod = logisticsMethod;
        this.shippingUrl = shippingUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.failedUrl = failedUrl;
        this.pdfUrl = pdfUrl;
        this.pdfObjectKey = pdfObjectKey;
        this.pageCount = pageCount;
        this.lastError = lastError;
    }

    public String status()             { return status; }
    public List<String> trackNumbers() { return trackNumbers; }
    public String logisticsMethod()    { return logisticsMethod; }
    public String shippingUrl()        { return shippingUrl; }
    public String thumbnailUrl()       { return thumbnailUrl; }
    public String failedUrl()          { return failedUrl; }
    public String pdfUrl()             { return pdfUrl; }
    public String pdfObjectKey()       { return pdfObjectKey; }
    public Integer pageCount()         { return pageCount; }
    public String lastError()          { return lastError; }

    public static HihumbirdLabel fromJson(Map<String, Object> o) {
        return new HihumbirdLabel(
                Json.str(o, "status"),
                Json.strList(o, "track_numbers"),
                Json.str(o, "logistics_method"),
                Json.str(o, "shipping_url"),
                Json.str(o, "thumbnail_url"),
                Json.str(o, "failed_url"),
                Json.str(o, "pdf_url"),
                Json.str(o, "pdf_object_key"),
                nullableInt(o.get("page_count")),
                Json.str(o, "last_error"));
    }

    private static Integer nullableInt(Object v) {
        return v instanceof Number ? ((Number) v).intValue() : null;
    }
}
