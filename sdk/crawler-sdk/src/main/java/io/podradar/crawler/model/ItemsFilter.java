package io.podradar.crawler.model;

import io.podradar.sdk.model.PageQuery;

import java.util.LinkedHashMap;
import java.util.Map;

/** Cross-run item filter for {@code GET /api/v1/hihumbird/items}. */
public final class ItemsFilter {
    private Long runId;
    private String q;
    private String salesOrderNo;
    private String productionBatchCode;
    private String productionOrderItemCode;
    private String trackNumber;
    private String statusName;
    private CrawlStatus crawlStatus;
    private PageQuery page = PageQuery.of(10, 0);

    private ItemsFilter() {}

    public static ItemsFilter empty() { return new ItemsFilter(); }

    public ItemsFilter withRunId(long id)                        { this.runId = id; return this; }
    public ItemsFilter withQuery(String text)                    { this.q = text; return this; }
    public ItemsFilter withSalesOrderNo(String no)               { this.salesOrderNo = no; return this; }
    public ItemsFilter withProductionBatchCode(String code)      { this.productionBatchCode = code; return this; }
    public ItemsFilter withProductionOrderItemCode(String code)  { this.productionOrderItemCode = code; return this; }
    public ItemsFilter withTrackNumber(String num)               { this.trackNumber = num; return this; }
    public ItemsFilter withStatusName(String name)               { this.statusName = name; return this; }
    public ItemsFilter withCrawlStatus(CrawlStatus s)            { this.crawlStatus = s; return this; }
    public ItemsFilter withPage(PageQuery page)                  { this.page = page; return this; }

    public Long runId()                       { return runId; }
    public String query()                     { return q; }
    public String salesOrderNo()              { return salesOrderNo; }
    public String productionBatchCode()       { return productionBatchCode; }
    public String productionOrderItemCode()   { return productionOrderItemCode; }
    public String trackNumber()               { return trackNumber; }
    public String statusName()                { return statusName; }
    public CrawlStatus crawlStatus()          { return crawlStatus; }
    public PageQuery page()                   { return page; }

    /** Query parameters as a key-value map (in insertion order). Values are pre-encoded strings. */
    public Map<String, String> toQueryParams() {
        Map<String, String> q = new LinkedHashMap<>();
        if (runId != null) q.put("run_id", String.valueOf(runId));
        if (this.q != null) q.put("q", this.q);
        if (salesOrderNo != null) q.put("sales_order_no", salesOrderNo);
        if (productionBatchCode != null) q.put("production_batch_code", productionBatchCode);
        if (productionOrderItemCode != null) q.put("production_order_item_code", productionOrderItemCode);
        if (trackNumber != null) q.put("track_number", trackNumber);
        if (statusName != null) q.put("status_name", statusName);
        if (crawlStatus != null) q.put("crawl_status", crawlStatus.wire());
        q.put("limit", String.valueOf(page.limit()));
        q.put("offset", String.valueOf(page.offset()));
        return q;
    }
}
