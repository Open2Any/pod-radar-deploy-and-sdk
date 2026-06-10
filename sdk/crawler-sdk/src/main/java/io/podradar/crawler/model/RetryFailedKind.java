package io.podradar.crawler.model;

/** Asset kind for the kind-scoped retry endpoint {@code POST /api/v1/hihumbird/retry-failed}. */
public enum RetryFailedKind {
    PRODUCT_IMAGE("product_image"),
    PRODUCTION_IMAGE("production_image"),
    SOURCE_IMAGE("source_image"),
    LABEL("label");

    private final String wire;

    RetryFailedKind(String wire) {
        this.wire = wire;
    }

    /** The on-the-wire value sent in the request body. */
    public String wire() {
        return wire;
    }
}
