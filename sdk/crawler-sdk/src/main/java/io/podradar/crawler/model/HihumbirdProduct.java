package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.Map;

/**
 * The {@code product} sub-block on a {@link HihumbirdItem}: product/SKU context derived from the
 * order item's source data. {@code processRoute} is the joined code-name; {@code attributeText} /
 * {@code skuText} are pre-formatted display strings. All fields may be {@code null}.
 */
public final class HihumbirdProduct {
    private final String id;
    private final String name;
    private final Integer quantity;
    private final String processRoute;
    private final String platformStatus;
    private final String expectDeliveryAt;
    private final String finishProductionAt;
    private final String styleCode;
    private final String styleName;
    private final String color;
    private final String size;
    private final String attributeText;
    private final String skuText;

    public HihumbirdProduct(String id, String name, Integer quantity, String processRoute,
                            String platformStatus, String expectDeliveryAt, String finishProductionAt,
                            String styleCode, String styleName, String color, String size,
                            String attributeText, String skuText) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.processRoute = processRoute;
        this.platformStatus = platformStatus;
        this.expectDeliveryAt = expectDeliveryAt;
        this.finishProductionAt = finishProductionAt;
        this.styleCode = styleCode;
        this.styleName = styleName;
        this.color = color;
        this.size = size;
        this.attributeText = attributeText;
        this.skuText = skuText;
    }

    public String id()                 { return id; }
    public String name()               { return name; }
    public Integer quantity()          { return quantity; }
    public String processRoute()       { return processRoute; }
    public String platformStatus()     { return platformStatus; }
    public String expectDeliveryAt()   { return expectDeliveryAt; }
    public String finishProductionAt() { return finishProductionAt; }
    public String styleCode()          { return styleCode; }
    public String styleName()          { return styleName; }
    public String color()              { return color; }
    public String size()               { return size; }
    public String attributeText()      { return attributeText; }
    public String skuText()            { return skuText; }

    public static HihumbirdProduct fromJson(Map<String, Object> o) {
        return new HihumbirdProduct(
                Json.str(o, "id"),
                Json.str(o, "name"),
                nullableInt(o.get("quantity")),
                Json.str(o, "process_route"),
                Json.str(o, "platform_status"),
                Json.str(o, "expect_delivery_at"),
                Json.str(o, "finish_production_at"),
                Json.str(o, "style_code"),
                Json.str(o, "style_name"),
                Json.str(o, "color"),
                Json.str(o, "size"),
                Json.str(o, "attribute_text"),
                Json.str(o, "sku_text"));
    }

    private static Integer nullableInt(Object v) {
        return v instanceof Number ? ((Number) v).intValue() : null;
    }
}
