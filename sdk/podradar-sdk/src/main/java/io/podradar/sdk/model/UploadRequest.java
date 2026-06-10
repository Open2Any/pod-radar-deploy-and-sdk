package io.podradar.sdk.model;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Builder for {@code POST /api/v1/images} — single image upload. */
public final class UploadRequest {

    public enum Mode { FILE, BYTES, URL }

    private final Mode mode;
    private final File file;
    private final byte[] bytes;
    private final String bytesMime;
    private final URI url;
    private String source;
    private String sourceId;
    private String title;
    private List<String> tags;
    private Map<String, Object> meta;

    private UploadRequest(Mode mode, File file, byte[] bytes, String bytesMime, URI url) {
        this.mode = mode;
        this.file = file;
        this.bytes = bytes;
        this.bytesMime = bytesMime;
        this.url = url;
    }

    public static UploadRequest fromFile(File file) {
        if (file == null || !file.isFile()) throw new IllegalArgumentException("file must exist");
        if (file.length() > 20L * 1024 * 1024) throw new IllegalArgumentException("file > 20MB");
        return new UploadRequest(Mode.FILE, file, null, null, null);
    }

    public static UploadRequest fromBytes(byte[] data, String mime) {
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(mime, "mime");
        if (data.length == 0) throw new IllegalArgumentException("data is empty");
        if (data.length > 20 * 1024 * 1024) throw new IllegalArgumentException("data > 20MB");
        return new UploadRequest(Mode.BYTES, null, data, mime, null);
    }

    public static UploadRequest fromUrl(URI imageUrl) {
        Objects.requireNonNull(imageUrl, "imageUrl");
        return new UploadRequest(Mode.URL, null, null, null, imageUrl);
    }

    public UploadRequest withSource(String source) { this.source = source; return this; }
    public UploadRequest withSourceId(String sourceId) { this.sourceId = sourceId; return this; }
    public UploadRequest withTitle(String title) { this.title = title; return this; }
    public UploadRequest withTags(List<String> tags) {
        this.tags = tags == null ? null : Collections.unmodifiableList(new java.util.ArrayList<>(tags));
        return this;
    }
    public UploadRequest withMeta(Map<String, Object> meta) {
        this.meta = meta == null ? null : Collections.unmodifiableMap(new LinkedHashMap<>(meta));
        return this;
    }

    public Mode mode() { return mode; }
    public File file() { return file; }
    public byte[] bytes() { return bytes; }
    public String bytesMime() { return bytesMime; }
    public URI url() { return url; }
    public String source() { return source; }
    public String sourceId() { return sourceId; }
    public String title() { return title; }
    public List<String> tags() { return tags; }
    public Map<String, Object> meta() { return meta; }

    public boolean isMultipart() { return mode == Mode.FILE || mode == Mode.BYTES; }
}
