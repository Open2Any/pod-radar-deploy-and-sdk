package io.podradar.crawler.model;

/** Per-item crawl outcome filter used by {@link ItemsFilter#withCrawlStatus}. */
public enum CrawlStatus {
    OK("ok"),
    FAILED("failed"),
    PARTIAL("partial");

    private final String wire;

    CrawlStatus(String wire) { this.wire = wire; }

    public String wire() { return wire; }
}
