package io.podradar.sdk.model;

import java.util.Objects;

/**
 * Immutable {@code limit}/{@code offset} pager shared by both SDKs.
 *
 * <p>Created with {@link #of(int, int)}; advance with {@link #next()} (offset += limit).
 * Validation rules:
 * <ul>
 *   <li>{@code limit} must be in {@code [1, 1000]} (each endpoint clamps further; server enforces).</li>
 *   <li>{@code offset} must be {@code >= 0}.</li>
 * </ul>
 */
public final class PageQuery {
    private final int limit;
    private final int offset;

    private PageQuery(int limit, int offset) {
        if (limit < 1 || limit > 1000) {
            throw new IllegalArgumentException("limit must be in [1, 1000], got " + limit);
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be >= 0, got " + offset);
        }
        this.limit = limit;
        this.offset = offset;
    }

    public static PageQuery of(int limit, int offset) {
        return new PageQuery(limit, offset);
    }

    public int limit() {
        return limit;
    }

    public int offset() {
        return offset;
    }

    /** Returns a new {@code PageQuery} advanced by one page (same limit, offset += limit). */
    public PageQuery next() {
        return new PageQuery(limit, offset + limit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageQuery)) return false;
        PageQuery other = (PageQuery) o;
        return limit == other.limit && offset == other.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(limit, offset);
    }

    @Override
    public String toString() {
        return "PageQuery{limit=" + limit + ", offset=" + offset + "}";
    }
}
