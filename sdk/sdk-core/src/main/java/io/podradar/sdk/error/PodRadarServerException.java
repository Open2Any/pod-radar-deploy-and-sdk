package io.podradar.sdk.error;

/** HTTP 5xx. Server-side failure; eligible for retry when {@code retryOnServerError(true)}. */
public class PodRadarServerException extends PodRadarException {
    private static final long serialVersionUID = 1L;

    public PodRadarServerException(int statusCode, String error, String requestId) {
        super(statusCode, error, requestId);
    }
}
