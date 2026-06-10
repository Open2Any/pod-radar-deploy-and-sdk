package io.podradar.sdk.error;

import java.util.Collections;
import java.util.Map;

/** HTTP 400 (bad request), 413 (payload too large), 415 (unsupported media type). */
public class PodRadarValidationException extends PodRadarException {
    private static final long serialVersionUID = 1L;

    private final Map<String, Object> details;

    public PodRadarValidationException(int statusCode, String error, String requestId, Map<String, Object> details) {
        super(statusCode, error, requestId);
        this.details = details == null ? Collections.emptyMap() : Collections.unmodifiableMap(details);
    }

    /** Field-level validation details, or an empty map if the server didn't supply any. */
    public Map<String, Object> details() {
        return details;
    }
}
