package io.podradar.sdk.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Minimal RFC 7578 {@code multipart/form-data} builder.
 *
 * <p>Used by image upload endpoints. Builds the body in memory and returns the bytes plus
 * the boundary token to be appended to the {@code Content-Type} header.
 */
public final class Multipart {
    private static final byte[] CRLF = {'\r', '\n'};
    private static final byte[] DASHES = {'-', '-'};

    private final String boundary;
    private final List<Part> parts = new ArrayList<>();

    public Multipart() {
        this.boundary = generateBoundary();
    }

    public Multipart addField(String name, String value) {
        parts.add(new Part(name, null, null, value.getBytes(StandardCharsets.UTF_8)));
        return this;
    }

    public Multipart addFile(String name, String filename, String contentType, byte[] data) {
        if (filename == null) filename = "blob";
        if (contentType == null) contentType = "application/octet-stream";
        parts.add(new Part(name, filename, contentType, data));
        return this;
    }

    public String contentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    public byte[] body() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (Part p : parts) {
                out.write(DASHES);
                out.write(boundary.getBytes(StandardCharsets.US_ASCII));
                out.write(CRLF);
                StringBuilder disp = new StringBuilder("Content-Disposition: form-data; name=\"")
                        .append(escapeHeader(p.name)).append('"');
                if (p.filename != null) {
                    disp.append("; filename=\"").append(escapeHeader(p.filename)).append('"');
                }
                out.write(disp.toString().getBytes(StandardCharsets.UTF_8));
                out.write(CRLF);
                if (p.contentType != null) {
                    out.write(("Content-Type: " + p.contentType).getBytes(StandardCharsets.US_ASCII));
                    out.write(CRLF);
                }
                out.write(CRLF);
                out.write(p.data);
                out.write(CRLF);
            }
            out.write(DASHES);
            out.write(boundary.getBytes(StandardCharsets.US_ASCII));
            out.write(DASHES);
            out.write(CRLF);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String escapeHeader(String s) {
        return s.replace("\"", "\\\"").replace("\r", "").replace("\n", "");
    }

    private static String generateBoundary() {
        byte[] rnd = new byte[16];
        ThreadLocalRandom.current().nextBytes(rnd);
        StringBuilder sb = new StringBuilder("----PodRadarSDKBoundary");
        for (byte b : rnd) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    private static final class Part {
        final String name;
        final String filename;
        final String contentType;
        final byte[] data;
        Part(String name, String filename, String contentType, byte[] data) {
            this.name = name;
            this.filename = filename;
            this.contentType = contentType;
            this.data = data;
        }
    }
}
