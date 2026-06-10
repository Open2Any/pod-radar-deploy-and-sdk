package io.podradar.sdk.error;

/** IO failure / timeout / DNS / TLS handshake. Status code is {@code 0}. */
public class PodRadarNetworkException extends PodRadarException {
    private static final long serialVersionUID = 1L;

    public PodRadarNetworkException(String error, Throwable cause) {
        super(error, cause);
    }
}
