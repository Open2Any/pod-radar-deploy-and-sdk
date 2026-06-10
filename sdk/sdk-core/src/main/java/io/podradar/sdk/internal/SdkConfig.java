package io.podradar.sdk.internal;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

/**
 * Internal config bag shared by both SDKs.
 *
 * <p>Each SDK exposes its own public {@code Builder} that ultimately constructs one of these.
 * Holds endpoint, API key, timeouts, UA, retry flag.
 */
public final class SdkConfig {
    private final URI endpoint;
    private final String apiKey;
    private final Duration connectTimeout;
    private final Duration requestTimeout;
    private final String userAgent;
    private final boolean retryOnServerError;
    private final int maxRetries;

    private SdkConfig(Builder b) {
        this.endpoint = Objects.requireNonNull(b.endpoint, "endpoint");
        this.apiKey = Objects.requireNonNull(b.apiKey, "apiKey");
        this.connectTimeout = b.connectTimeout;
        this.requestTimeout = b.requestTimeout;
        this.userAgent = b.userAgent;
        this.retryOnServerError = b.retryOnServerError;
        this.maxRetries = b.maxRetries;
    }

    public URI endpoint() { return endpoint; }
    public String apiKey() { return apiKey; }
    public Duration connectTimeout() { return connectTimeout; }
    public Duration requestTimeout() { return requestTimeout; }
    public String userAgent() { return userAgent; }
    public boolean retryOnServerError() { return retryOnServerError; }
    public int maxRetries() { return maxRetries; }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private URI endpoint;
        private String apiKey;
        private Duration connectTimeout = Duration.ofSeconds(10);
        private Duration requestTimeout = Duration.ofSeconds(30);
        private String userAgent = "podradar-java-sdk/0.1.0";
        private boolean retryOnServerError = false;
        private int maxRetries = 2;

        public Builder endpoint(String url) {
            if (url == null) throw new IllegalArgumentException("endpoint is null");
            this.endpoint = URI.create(url);
            return this;
        }

        public Builder endpoint(URI uri) {
            this.endpoint = Objects.requireNonNull(uri, "endpoint");
            return this;
        }

        public Builder apiKey(String key) {
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException("apiKey is empty");
            }
            this.apiKey = key;
            return this;
        }

        public Builder connectTimeout(Duration d) {
            if (d == null || d.isNegative() || d.isZero()) {
                throw new IllegalArgumentException("connectTimeout must be positive");
            }
            this.connectTimeout = d;
            return this;
        }

        public Builder requestTimeout(Duration d) {
            if (d == null || d.isNegative() || d.isZero()) {
                throw new IllegalArgumentException("requestTimeout must be positive");
            }
            this.requestTimeout = d;
            return this;
        }

        public Builder userAgent(String ua) {
            if (ua == null || ua.isEmpty()) {
                throw new IllegalArgumentException("userAgent is empty");
            }
            this.userAgent = ua;
            return this;
        }

        public Builder retryOnServerError(boolean on) {
            this.retryOnServerError = on;
            return this;
        }

        public Builder maxRetries(int n) {
            if (n < 0 || n > 10) {
                throw new IllegalArgumentException("maxRetries must be in [0, 10]");
            }
            this.maxRetries = n;
            return this;
        }

        public SdkConfig build() {
            return new SdkConfig(this);
        }
    }
}
