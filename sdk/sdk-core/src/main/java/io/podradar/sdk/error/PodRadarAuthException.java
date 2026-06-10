package io.podradar.sdk.error;

/** HTTP 401 (unauthenticated) and 403 (insufficient scope). Do not retry. */
public class PodRadarAuthException extends PodRadarException {
    private static final long serialVersionUID = 1L;

    public PodRadarAuthException(int statusCode, String error, String requestId) {
        super(statusCode, error, requestId);
    }
}
