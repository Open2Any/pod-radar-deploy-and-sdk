package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/**
 * Response of {@code POST /api/v1/fangguo/items/{id}/retry}: {@code status} is {@code "queued"}
 * and {@code orderUnitId} echoes the retried order unit, with the count of failed assets/labels
 * that were re-enqueued. A missing item yields a 404 ({@code PodRadarNotFoundException}).
 */
public final class FangguoItemRetryResponse {
    private final String status;
    private final long orderUnitId;
    private final int requeuedAssets;
    private final int requeuedLabels;

    public FangguoItemRetryResponse(String status, long orderUnitId, int requeuedAssets,
                                    int requeuedLabels) {
        this.status = status;
        this.orderUnitId = orderUnitId;
        this.requeuedAssets = requeuedAssets;
        this.requeuedLabels = requeuedLabels;
    }

    public String status()      { return status; }
    public long orderUnitId()   { return orderUnitId; }
    public int requeuedAssets() { return requeuedAssets; }
    public int requeuedLabels() { return requeuedLabels; }

    public static FangguoItemRetryResponse fromJson(Map<String, Object> o) {
        return new FangguoItemRetryResponse(
                Json.str(o, "status"),
                Json.lng(o, "order_unit_id"),
                Json.integ(o, "requeued_assets"),
                Json.integ(o, "requeued_labels"));
    }
}
