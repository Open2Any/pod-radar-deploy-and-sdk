package io.podradar.sdk.internal;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {

    @Test
    void writesPrimitives() {
        assertEquals("null", JsonWriter.write(null));
        assertEquals("true", JsonWriter.write(Boolean.TRUE));
        assertEquals("false", JsonWriter.write(Boolean.FALSE));
        assertEquals("42", JsonWriter.write(42));
        assertEquals("42", JsonWriter.write(42L));
        assertEquals("\"hi\"", JsonWriter.write("hi"));
    }

    @Test
    void escapesStringSpecials() {
        assertEquals("\"a\\\"b\"", JsonWriter.write("a\"b"));
        assertEquals("\"a\\\\b\"", JsonWriter.write("a\\b"));
        assertEquals("\"line1\\nline2\"", JsonWriter.write("line1\nline2"));
        assertEquals("\"\\t\"", JsonWriter.write("\t"));
        assertEquals("\"\\u0001\"", JsonWriter.write(String.valueOf((char) 1)));
    }

    @Test
    void writesArrays() {
        assertEquals("[]", JsonWriter.write(Arrays.asList()));
        assertEquals("[1,2,3]", JsonWriter.write(Arrays.asList(1L, 2L, 3L)));
        assertEquals("[1,\"x\",null]", JsonWriter.write(Arrays.asList(1L, "x", null)));
    }

    @Test
    void writesObjectsInInsertionOrder() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("b", 1L);
        m.put("a", "x");
        m.put("c", null);
        assertEquals("{\"b\":1,\"a\":\"x\",\"c\":null}", JsonWriter.write(m));
    }

    @Test
    void roundTripsViaReader() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("nums", Arrays.asList(1L, 2L, 3L));
        m.put("ok", Boolean.TRUE);
        m.put("nothing", null);
        m.put("name", "hi\n\"there\"");
        String json = JsonWriter.write(m);
        Object back = JsonReader.parse(json);
        assertEquals(m, back);
    }

    @Test
    void rejectsNonFiniteDouble() {
        assertThrows(IllegalArgumentException.class, () -> JsonWriter.write(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> JsonWriter.write(Double.POSITIVE_INFINITY));
    }

    @Test
    void rejectsUnknownType() {
        assertThrows(IllegalArgumentException.class, () -> JsonWriter.write(new Object()));
    }
}
