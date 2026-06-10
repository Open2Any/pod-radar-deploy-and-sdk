package io.podradar.sdk.error;

/**
 * Base type for every exception thrown by podradar-sdk and crawler-sdk.
 *
 * <p>Subclasses correspond to HTTP status code groups; see {@link io.podradar.sdk.error}
 * package classes. All are unchecked so business code never has to write {@code throws}.
 *
 * <p>{@link #requestId()} is the server-side request id (echoed back in the
 * {@code X-Request-Id} response header); pass it to support when filing a ticket.
 */
public class PodRadarException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int statusCode;
    private final String error;
    private final String requestId;

    public PodRadarException(int statusCode, String error, String requestId) {
        super(buildMessage(statusCode, error, requestId));
        this.statusCode = statusCode;
        this.error = error;
        this.requestId = requestId;
    }

    public PodRadarException(int statusCode, String error, String requestId, Throwable cause) {
        super(buildMessage(statusCode, error, requestId), cause);
        this.statusCode = statusCode;
        this.error = error;
        this.requestId = requestId;
    }

    public PodRadarException(String error, Throwable cause) {
        super(error, cause);
        this.statusCode = 0;
        this.error = error;
        this.requestId = null;
    }

    /** HTTP status code; {@code 0} for network-layer failures. */
    public int statusCode() {
        return statusCode;
    }

    /** Server-supplied error string, or the local cause message for network errors. */
    public String error() {
        return error;
    }

    /** Server request id (may be {@code null} for network errors / older server versions). */
    public String requestId() {
        return requestId;
    }

    private static String buildMessage(int statusCode, String error, String requestId) {
        StringBuilder sb = new StringBuilder();
        if (statusCode > 0) sb.append("HTTP ").append(statusCode).append(": ");
        sb.append(error != null ? error : "(no error message)");
        if (requestId != null && !requestId.isEmpty()) sb.append(" [request_id=").append(requestId).append("]");
        return sb.toString();
    }
}
