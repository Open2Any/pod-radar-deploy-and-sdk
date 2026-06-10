package io.podradar.sdk.error;

/** HTTP 404. Resource doesn't exist (run / image / key). */
public class PodRadarNotFoundException extends PodRadarException {
    private static final long serialVersionUID = 1L;

    public PodRadarNotFoundException(String error, String requestId) {
        super(404, error, requestId);
    }
}
