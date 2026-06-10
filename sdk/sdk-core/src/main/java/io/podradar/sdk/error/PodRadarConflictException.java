package io.podradar.sdk.error;

/**
 * HTTP 409. Resource state conflict. In crawler-sdk this represents
 * "another run is already running" — a common business-side catch path.
 */
public class PodRadarConflictException extends PodRadarException {
    private static final long serialVersionUID = 1L;

    public PodRadarConflictException(String error, String requestId) {
        super(409, error, requestId);
    }
}
