package io.podradar.sdk.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal recursive-descent JSON parser.
 *
 * <p>Returns Java objects keyed by their JSON shape:
 * <ul>
 *   <li>{@code null} → {@code null}</li>
 *   <li>boolean → {@link Boolean}</li>
 *   <li>integer numbers fitting in {@code long} → {@link Long}; otherwise {@link Double}</li>
 *   <li>string → {@link String}</li>
 *   <li>array → {@code List<Object>}</li>
 *   <li>object → {@code Map<String,Object>} (insertion order preserved)</li>
 * </ul>
 *
 * <p>Not thread-safe; create one instance per parse.
 */
public final class JsonReader {
    private final String src;
    private int pos;

    private JsonReader(String src) {
        this.src = src;
        this.pos = 0;
    }

    /** Parse a complete JSON document. Throws {@link JsonParseException} on malformed input. */
    public static Object parse(String json) {
        if (json == null) throw new JsonParseException("input is null");
        JsonReader r = new JsonReader(json);
        r.skipWs();
        Object v = r.readValue();
        r.skipWs();
        if (r.pos != r.src.length()) {
            throw r.fail("unexpected trailing content");
        }
        return v;
    }

    /** Convenience: parse and cast to {@code Map<String,Object>}. */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String json) {
        Object v = parse(json);
        if (!(v instanceof Map)) {
            throw new JsonParseException("expected JSON object, got " + typeOf(v));
        }
        return (Map<String, Object>) v;
    }

    private Object readValue() {
        if (pos >= src.length()) throw fail("unexpected end of input");
        char c = src.charAt(pos);
        switch (c) {
            case '{': return readObject();
            case '[': return readArray();
            case '"': return readString();
            case 't': case 'f': return readBoolean();
            case 'n': return readNull();
            default:
                if (c == '-' || (c >= '0' && c <= '9')) return readNumber();
                throw fail("unexpected character '" + c + "'");
        }
    }

    private Map<String, Object> readObject() {
        expect('{');
        Map<String, Object> obj = new LinkedHashMap<>();
        skipWs();
        if (peek() == '}') { pos++; return obj; }
        while (true) {
            skipWs();
            String key = readString();
            skipWs();
            expect(':');
            skipWs();
            Object value = readValue();
            obj.put(key, value);
            skipWs();
            char c = peek();
            if (c == ',') { pos++; continue; }
            if (c == '}') { pos++; return obj; }
            throw fail("expected ',' or '}' in object, got '" + c + "'");
        }
    }

    private List<Object> readArray() {
        expect('[');
        List<Object> arr = new ArrayList<>();
        skipWs();
        if (peek() == ']') { pos++; return arr; }
        while (true) {
            skipWs();
            arr.add(readValue());
            skipWs();
            char c = peek();
            if (c == ',') { pos++; continue; }
            if (c == ']') { pos++; return arr; }
            throw fail("expected ',' or ']' in array, got '" + c + "'");
        }
    }

    private String readString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (pos < src.length()) {
            char c = src.charAt(pos++);
            if (c == '"') return sb.toString();
            if (c == '\\') {
                if (pos >= src.length()) throw fail("unexpected end of string");
                char esc = src.charAt(pos++);
                switch (esc) {
                    case '"':  sb.append('"');  break;
                    case '\\': sb.append('\\'); break;
                    case '/':  sb.append('/');  break;
                    case 'b':  sb.append('\b'); break;
                    case 'f':  sb.append('\f'); break;
                    case 'n':  sb.append('\n'); break;
                    case 'r':  sb.append('\r'); break;
                    case 't':  sb.append('\t'); break;
                    case 'u':
                        if (pos + 4 > src.length()) throw fail("incomplete \\u escape");
                        int cp = Integer.parseInt(src.substring(pos, pos + 4), 16);
                        sb.append((char) cp);
                        pos += 4;
                        break;
                    default:
                        throw fail("invalid escape \\" + esc);
                }
            } else if (c < 0x20) {
                throw fail("unescaped control char in string");
            } else {
                sb.append(c);
            }
        }
        throw fail("unterminated string");
    }

    private Object readNumber() {
        int start = pos;
        if (peek() == '-') pos++;
        while (pos < src.length() && isNumeric(src.charAt(pos))) pos++;
        String raw = src.substring(start, pos);
        boolean isFloat = raw.indexOf('.') >= 0 || raw.indexOf('e') >= 0 || raw.indexOf('E') >= 0;
        try {
            if (isFloat) {
                return Double.parseDouble(raw);
            }
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            try {
                return Double.parseDouble(raw);
            } catch (NumberFormatException e2) {
                throw fail("invalid number '" + raw + "'");
            }
        }
    }

    private Boolean readBoolean() {
        if (src.startsWith("true", pos)) { pos += 4; return Boolean.TRUE; }
        if (src.startsWith("false", pos)) { pos += 5; return Boolean.FALSE; }
        throw fail("expected boolean");
    }

    private Object readNull() {
        if (src.startsWith("null", pos)) { pos += 4; return null; }
        throw fail("expected null");
    }

    private void skipWs() {
        while (pos < src.length()) {
            char c = src.charAt(pos);
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') pos++;
            else break;
        }
    }

    private char peek() {
        if (pos >= src.length()) throw fail("unexpected end of input");
        return src.charAt(pos);
    }

    private void expect(char c) {
        if (pos >= src.length() || src.charAt(pos) != c) {
            throw fail("expected '" + c + "'");
        }
        pos++;
    }

    private static boolean isNumeric(char c) {
        return (c >= '0' && c <= '9') || c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-';
    }

    private JsonParseException fail(String msg) {
        return new JsonParseException(msg + " at offset " + pos);
    }

    private static String typeOf(Object v) {
        if (v == null) return "null";
        if (v instanceof Map) return "object";
        if (v instanceof List) return "array";
        return v.getClass().getSimpleName();
    }

    /** Thrown when input cannot be parsed as JSON. Unchecked. */
    public static final class JsonParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public JsonParseException(String message) {
            super(message);
        }
    }
}
