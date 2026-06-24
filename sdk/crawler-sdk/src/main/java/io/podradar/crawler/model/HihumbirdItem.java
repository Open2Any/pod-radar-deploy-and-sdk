package io.podradar.crawler.model;

import io.podradar.sdk.internal.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A hihumbird production order item with its harvested {@code assets} (product / production / source
 * images), a {@code product} context block, and an optional shipping {@code label}. Returned by
 * {@code GET /runs/{id}/items} and {@code GET /items}.
 *
 * <p>The server emits a NESTED shape — image assets live in {@link #assets()} keyed by
 * {@link HihumbirdAsset#assetKind()} ({@code product_image} / {@code production_image} /
 * {@code source_image}), NOT as flat top-level fields. To get the product mockup, filter
 * {@code item.assets()} for {@code "product_image"}.
 */
public final class HihumbirdItem {
    private final long id;
    private final Long runId;
    private final Long orderItemId;
    private final String detailSource;
    private final String productionOrderItemId;
    private final String productionOrderItemCode;
    private final String orderId;
    private final String relCode;
    private final String relThirdId;
    private final String salesOrderNo;
    private final String productionBatchId;
    private final String productionBatchCode;
    private final String statusName;
    private final String sourceCreatedAt;
    private final Long accountId;
    private final String accountName;
    private final String accountUsername;
    private final String system;
    private final HihumbirdProduct product;
    private final List<HihumbirdAsset> assets;
    private final HihumbirdLabel label;

    public HihumbirdItem(long id, Long runId, Long orderItemId, String detailSource,
                         String productionOrderItemId, String productionOrderItemCode, String orderId,
                         String relCode, String relThirdId, String salesOrderNo, String productionBatchId,
                         String productionBatchCode, String statusName, String sourceCreatedAt,
                         Long accountId, String accountName, String accountUsername, String system,
                         HihumbirdProduct product, List<HihumbirdAsset> assets, HihumbirdLabel label) {
        this.id = id;
        this.runId = runId;
        this.orderItemId = orderItemId;
        this.detailSource = detailSource;
        this.productionOrderItemId = productionOrderItemId;
        this.productionOrderItemCode = productionOrderItemCode;
        this.orderId = orderId;
        this.relCode = relCode;
        this.relThirdId = relThirdId;
        this.salesOrderNo = salesOrderNo;
        this.productionBatchId = productionBatchId;
        this.productionBatchCode = productionBatchCode;
        this.statusName = statusName;
        this.sourceCreatedAt = sourceCreatedAt;
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountUsername = accountUsername;
        this.system = system;
        this.product = product;
        this.assets = assets;
        this.label = label;
    }

    public long id()                        { return id; }
    public Long runId()                     { return runId; }
    public Long orderItemId()               { return orderItemId; }
    public String detailSource()            { return detailSource; }
    public String productionOrderItemId()   { return productionOrderItemId; }
    public String productionOrderItemCode() { return productionOrderItemCode; }
    public String orderId()                 { return orderId; }
    public String relCode()                 { return relCode; }
    public String relThirdId()              { return relThirdId; }
    public String salesOrderNo()            { return salesOrderNo; }
    public String productionBatchId()       { return productionBatchId; }
    public String productionBatchCode()     { return productionBatchCode; }
    public String statusName()              { return statusName; }
    public String sourceCreatedAt()         { return sourceCreatedAt; }
    /** Upstream account this item was crawled under, or {@code null} (.env fallback / legacy). */
    public Long accountId()                 { return accountId; }
    public String accountName()             { return accountName; }
    /** The account's upstream login username, or {@code null}. */
    public String accountUsername()         { return accountUsername; }
    /** Always {@code "hihumbird"}. */
    public String system()                  { return system; }
    public HihumbirdProduct product()       { return product; }
    public List<HihumbirdAsset> assets()    { return assets; }
    /** The order's shipping label, or {@code null} if none. */
    public HihumbirdLabel label()           { return label; }

    /** Convenience: the first asset of the given kind ({@code product_image} etc.), or {@code null}. */
    public HihumbirdAsset asset(String assetKind) {
        if (assetKind == null) return null;
        for (HihumbirdAsset a : assets) {
            if (assetKind.equals(a.assetKind())) return a;
        }
        return null;
    }

    public static HihumbirdItem fromJson(Map<String, Object> o) {
        List<HihumbirdAsset> assets = new ArrayList<>();
        for (Object raw : Json.list(o, "assets")) {
            assets.add(HihumbirdAsset.fromJson(Json.asMap(raw)));
        }
        Object labelRaw = o.get("label");
        HihumbirdLabel label = labelRaw instanceof Map ? HihumbirdLabel.fromJson(Json.asMap(labelRaw)) : null;
        Object productRaw = o.get("product");
        HihumbirdProduct product = productRaw instanceof Map
                ? HihumbirdProduct.fromJson(Json.asMap(productRaw))
                : null;
        return new HihumbirdItem(
                Json.lng(o, "id"),
                nullableLong(o.get("run_id")),
                nullableLong(o.get("order_item_id")),
                Json.str(o, "detail_source"),
                Json.str(o, "production_order_item_id"),
                Json.str(o, "production_order_item_code"),
                Json.str(o, "order_id"),
                Json.str(o, "rel_code"),
                Json.str(o, "rel_third_id"),
                Json.str(o, "sales_order_no"),
                Json.str(o, "production_batch_id"),
                Json.str(o, "production_batch_code"),
                Json.str(o, "status_name"),
                Json.str(o, "source_created_at"),
                nullableLong(o.get("account_id")),
                Json.str(o, "account_name"),
                Json.str(o, "account_username"),
                Json.str(o, "system"),
                product,
                Collections.unmodifiableList(assets),
                label);
    }

    private static Long nullableLong(Object v) {
        return v instanceof Number ? ((Number) v).longValue() : null;
    }
}
