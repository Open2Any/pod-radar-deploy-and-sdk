package io.podradar.sdk.internal;

import java.util.List;
import java.util.Map;

/**
 * Minimal JSON writer matching {@link JsonReader}'s type contract.
 *
 * <p>Accepts {@code null}, {@link Boolean}, {@link Number} (Long/Integer/Short/Byte rendered
 * as integers; Double/Float as decimals), {@link CharSequence}, {@code List}, {@code Map}
 * (keys coerced via {@link Object#toString()}). Any other value throws
 * {@link IllegalArgumentException}.
 */
public final class JsonWriter {
    private JsonWriter() {}

    public static String write(Object value) {
        StringBuilder sb = new StringBuilder();
        writeValue(sb, value);
        return sb.toString();
    }

    private static void writeValue(StringBuilder sb, Object v) {
        if (v == null) {
            sb.append("null");
        } else if (v instanceof Boolean) {
            sb.append(((Boolean) v) ? "true" : "false");
        } else if (v instanceof Long || v instanceof Integer || v instanceof Short || v instanceof Byte) {
            sb.append(v.toString());
        } else if (v instanceof Double || v instanceof Float) {
            double d = ((Number) v).doubleValue();
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                throw new IllegalArgumentException("cannot serialize non-finite number: " + d);
            }
            sb.append(v.toString());
        } else if (v instanceof Number) {
            sb.append(v.toString());
        } else if (v instanceof CharSequence) {
            writeString(sb, v.toString());
        } else if (v instanceof Map) {
            writeObject(sb, (Map<?, ?>) v);
        } else if (v instanceof List) {
            writeArray(sb, (List<?>) v);
        } else {
            throw new IllegalArgumentException("cannot serialize type " + v.getClass().getName());
        }
    }

    private static void writeObject(StringBuilder sb, Map<?, ?> map) {
        sb.append('{');
        boolean first = true;
        for (Map.Entry<?, ?> e : map.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            Object key = e.getKey();
            if (key == null) throw new IllegalArgumentException("null map key");
            writeString(sb, key.toString());
            sb.append(':');
            writeValue(sb, e.getValue());
        }
        sb.append('}');
    }

    private static void writeArray(StringBuilder sb, List<?> list) {
        sb.append('[');
        boolean first = true;
        for (Object item : list) {
            if (!first) sb.append(',');
            first = false;
            writeValue(sb, item);
        }
        sb.append(']');
    }

    private static void writeString(StringBuilder sb, String s) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b");  break;
                case '\f': sb.append("\\f");  break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }
}
