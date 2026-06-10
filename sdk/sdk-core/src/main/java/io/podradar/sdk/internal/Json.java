package io.podradar.sdk.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * Tiny safe-accessor helpers over the {@code Map<String,Object>} that {@link JsonReader}
 * returns. Centralizes "is this key a string / long / map / list" coercion so DTOs stay
 * compact.
 */
public final class Json {
    private Json() {}

    public static String str(Map<String, Object> obj, String key) {
        Object v = obj.get(key);
        return v == null ? null : String.valueOf(v);
    }

    public static String reqStr(Map<String, Object> obj, String key) {
        String v = str(obj, key);
        if (v == null) throw new IllegalStateException("missing field '" + key + "'");
        return v;
    }

    public static long lng(Map<String, Object> obj, String key) {
        Object v = obj.get(key);
        if (v == null) return 0L;
        if (v instanceof Number) return ((Number) v).longValue();
        return Long.parseLong(v.toString());
    }

    public static OptionalLong optLong(Map<String, Object> obj, String key) {
        Object v = obj.get(key);
        if (v == null) return OptionalLong.empty();
        if (v instanceof Number) return OptionalLong.of(((Number) v).longValue());
        return OptionalLong.of(Long.parseLong(v.toString()));
    }

    public static int integ(Map<String, Object> obj, String key) {
        return (int) lng(obj, key);
    }

    public static OptionalInt optInt(Map<String, Object> obj, String key) {
        Object v = obj.get(key);
        if (v == null) return OptionalInt.empty();
        if (v instanceof Number) return OptionalInt.of(((Number) v).intValue());
        return OptionalInt.of(Integer.parseInt(v.toString()));
    }

    public static double dbl(Map<String, Object> obj, String key) {
        Object v = obj.get(key);
        if (v == null) return 0.0;
        if (v instanceof Number) return ((Number) v).doubleValue();
        return Double.parseDouble(v.toString());
    }

    public static boolean bool(Map<String, Object> obj, String key) {
        Object v = obj.get(key);
        return v instanceof Boolean && (Boolean) v;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> obj(Map<String, Object> obj, String key) {
        Object v = obj.get(key);
        if (v == null) return Collections.emptyMap();
        if (!(v instanceof Map)) {
            throw new IllegalStateException("field '" + key + "' is not an object");
        }
        return (Map<String, Object>) v;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> list(Map<String, Object> obj, String key) {
        Object v = obj.get(key);
        if (v == null) return Collections.emptyList();
        if (!(v instanceof List)) {
            throw new IllegalStateException("field '" + key + "' is not an array");
        }
        return (List<Object>) v;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(Object v) {
        if (v == null) return Collections.emptyMap();
        if (!(v instanceof Map)) {
            throw new IllegalStateException("expected object, got " + v.getClass().getSimpleName());
        }
        return (Map<String, Object>) v;
    }

    public static List<String> strList(Map<String, Object> obj, String key) {
        List<Object> raw = list(obj, key);
        List<String> out = new java.util.ArrayList<>(raw.size());
        for (Object x : raw) {
            out.add(x == null ? null : String.valueOf(x));
        }
        return Collections.unmodifiableList(out);
    }
}
