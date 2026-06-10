package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** A single hihumbird-sourced item (label / image / pdf), returned by run/items endpoints. */
public final class HihumbirdItem {
    private final long id;
    private final String assetKind;
    private final String externalKey;
    private final String salesOrderNo;
    private final String productionBatchCode;
    private final String productionOrderItemCode;
    private final String trackNumber;
    private final String statusName;
    private final String styleCode;
    private final String styleName;
    private final String color;
    private final String size;
    private final String processRouteCode;
    private final String processRouteName;
    private final String labelImage;
    private final List<String> labelPages;
    private final String title;
    private final Integer width;
    private final Integer height;
    private final String thumb;
    private final String full;
    private final String status;
    private final String lastError;
    private final String createdAt;
    private final String updatedAt;

    public HihumbirdItem(long id, String assetKind, String externalKey, String salesOrderNo,
                         String productionBatchCode, String productionOrderItemCode, String trackNumber,
                         String statusName, String styleCode, String styleName, String color, String size,
                         String processRouteCode, String processRouteName, String labelImage,
                         List<String> labelPages, String title, Integer width, Integer height,
                         String thumb, String full, String status, String lastError,
                         String createdAt, String updatedAt) {
        this.id = id;
        this.assetKind = assetKind;
        this.externalKey = externalKey;
        this.salesOrderNo = salesOrderNo;
        this.productionBatchCode = productionBatchCode;
        this.productionOrderItemCode = productionOrderItemCode;
        this.trackNumber = trackNumber;
        this.statusName = statusName;
        this.styleCode = styleCode;
        this.styleName = styleName;
        this.color = color;
        this.size = size;
        this.processRouteCode = processRouteCode;
        this.processRouteName = processRouteName;
        this.labelImage = labelImage;
        this.labelPages = labelPages;
        this.title = title;
        this.width = width;
        this.height = height;
        this.thumb = thumb;
        this.full = full;
        this.status = status;
        this.lastError = lastError;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long id()                            { return id; }
    public String assetKind()                   { return assetKind; }
    public String externalKey()                 { return externalKey; }
    public String salesOrderNo()                { return salesOrderNo; }
    public String productionBatchCode()         { return productionBatchCode; }
    public String productionOrderItemCode()     { return productionOrderItemCode; }
    public String trackNumber()                 { return trackNumber; }
    public String statusName()                  { return statusName; }
    public String styleCode()                   { return styleCode; }
    public String styleName()                   { return styleName; }
    public String color()                       { return color; }
    public String size()                        { return size; }
    public String processRouteCode()            { return processRouteCode; }
    public String processRouteName()            { return processRouteName; }
    public String labelImage()                  { return labelImage; }
    public List<String> labelPages()            { return labelPages; }
    public String title()                       { return title; }
    public Integer width()                      { return width; }
    public Integer height()                     { return height; }
    public String thumb()                       { return thumb; }
    public String full()                        { return full; }
    public String status()                      { return status; }
    public String lastError()                   { return lastError; }
    public String createdAt()                   { return createdAt; }
    public String updatedAt()                   { return updatedAt; }

    public static HihumbirdItem fromJson(Map<String, Object> o) {
        List<String> pages = new ArrayList<>();
        if (o.get("label_pages") instanceof List) {
            for (Object x : Json.list(o, "label_pages")) {
                pages.add(x == null ? null : String.valueOf(x));
            }
        }
        return new HihumbirdItem(
                Json.lng(o, "id"),
                Json.str(o, "asset_kind"),
                Json.str(o, "external_key"),
                Json.str(o, "sales_order_no"),
                Json.str(o, "production_batch_code"),
                Json.str(o, "production_order_item_code"),
                Json.str(o, "track_number"),
                Json.str(o, "status_name"),
                Json.str(o, "style_code"),
                Json.str(o, "style_name"),
                Json.str(o, "color"),
                Json.str(o, "size"),
                Json.str(o, "process_route_code"),
                Json.str(o, "process_route_name"),
                Json.str(o, "label_image"),
                Collections.unmodifiableList(pages),
                Json.str(o, "title"),
                nullableInt(o.get("width")),
                nullableInt(o.get("height")),
                Json.str(o, "thumb"),
                Json.str(o, "full"),
                Json.str(o, "status"),
                Json.str(o, "last_error"),
                Json.str(o, "created_at"),
                Json.str(o, "updated_at"));
    }

    private static Integer nullableInt(Object v) {
        return v instanceof Number ? ((Number) v).intValue() : null;
    }
}
