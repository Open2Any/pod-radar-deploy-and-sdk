package io.podradar.sdk.error;

/**
 * HTTP 429. Throttled by the server.
 *
 * <p>Only the main system can throw this — the crawler service has no per-key rate
 * limit by design. The class lives in sdk-core to keep both SDKs sharing one
 * exception tree.
 *
 * <p>{@link #retryAfterSeconds()} echoes the server's {@code Retry-After} response
 * header; {@code 0} means the header was absent.
 */
public class PodRadarRateLimitException extends PodRadarException {
    private static final long serialVersionUID = 1L;

    private final long retryAfterSeconds;

    public PodRadarRateLimitException(String error, String requestId, long retryAfterSeconds) {
        super(429, error, requestId);
        this.retryAfterSeconds = Math.max(0, retryAfterSeconds);
    }

    public long retryAfterSeconds() {
        return retryAfterSeconds;
    }
}
