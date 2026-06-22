package io.podradar.crawler.model;

import io.podradar.sdk.model.PageQuery;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cross-run item filter for {@code GET /api/v1/fangguo/items}. Narrower than hihumbird's
 * {@code ItemsFilter}: fangguo supports only {@code run_id}, a free-text {@code q},
 * {@code ship_status}, and {@code crawl_status} (plus paging). The free-text {@code q} matches
 * tid / barcode / sku_ext_code / factory_encode / oid / store_name / shop_name.
 */
public final class FangguoItemsFilter {
    private Long runId;
    private String q;
    private String shipStatus;
    private FangguoCrawlStatus crawlStatus;
    private PageQuery page = PageQuery.of(10, 0);

    private FangguoItemsFilter() {}

    public static FangguoItemsFilter empty() { return new FangguoItemsFilter(); }

    public FangguoItemsFilter withRunId(long id)                  { this.runId = id; return this; }
    public FangguoItemsFilter withQuery(String text)              { this.q = text; return this; }
    public FangguoItemsFilter withShipStatus(String status)       { this.shipStatus = status; return this; }
    public FangguoItemsFilter withCrawlStatus(FangguoCrawlStatus s) { this.crawlStatus = s; return this; }
    public FangguoItemsFilter withPage(PageQuery page)            { this.page = page; return this; }

    public Long runId()                  { return runId; }
    public String query()                { return q; }
    public String shipStatus()           { return shipStatus; }
    public FangguoCrawlStatus crawlStatus() { return crawlStatus; }
    public PageQuery page()              { return page; }

    /** Query parameters as a key-value map (in insertion order). */
    public Map<String, String> toQueryParams() {
        Map<String, String> params = new LinkedHashMap<>();
        if (runId != null) params.put("run_id", String.valueOf(runId));
        if (q != null) params.put("q", q);
        if (shipStatus != null) params.put("ship_status", shipStatus);
        if (crawlStatus != null) params.put("crawl_status", crawlStatus.wire());
        params.put("limit", String.valueOf(page.limit()));
        params.put("offset", String.valueOf(page.offset()));
        return params;
    }
}
