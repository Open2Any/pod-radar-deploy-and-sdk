package io.podradar.sdk.internal;

import io.podradar.sdk.internal.JsonReader.JsonParseException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderTest {

    @Test
    void parsesPrimitives() {
        assertNull(JsonReader.parse("null"));
        assertEquals(Boolean.TRUE, JsonReader.parse("true"));
        assertEquals(Boolean.FALSE, JsonReader.parse("false"));
        assertEquals(123L, JsonReader.parse("123"));
        assertEquals(-7L, JsonReader.parse("-7"));
        assertEquals(1.5d, (Double) JsonReader.parse("1.5"));
        assertEquals(1.5e2d, (Double) JsonReader.parse("1.5e2"));
        assertEquals("hi", JsonReader.parse("\"hi\""));
    }

    @Test
    void parsesEscapes() {
        assertEquals("a\"b\\c\n", JsonReader.parse("\"a\\\"b\\\\c\\n\""));
        assertEquals("é", JsonReader.parse("\"\\u00e9\""));
    }

    @Test
    void parsesArray() {
        Object v = JsonReader.parse("[1,\"x\",null,true]");
        assertTrue(v instanceof List);
        List<?> list = (List<?>) v;
        assertEquals(4, list.size());
        assertEquals(1L, list.get(0));
        assertEquals("x", list.get(1));
        assertNull(list.get(2));
        assertEquals(Boolean.TRUE, list.get(3));
    }

    @Test
    void parsesObjectPreservesInsertionOrder() {
        Map<String, Object> obj = JsonReader.parseObject("{\"b\":1,\"a\":2,\"c\":3}");
        assertEquals(List.of("b", "a", "c"), List.copyOf(obj.keySet()));
    }

    @Test
    void parsesNested() {
        Map<String, Object> obj = JsonReader.parseObject("{\"x\":{\"y\":[1,2]},\"z\":\"ok\"}");
        @SuppressWarnings("unchecked")
        Map<String, Object> x = (Map<String, Object>) obj.get("x");
        @SuppressWarnings("unchecked")
        List<Object> y = (List<Object>) x.get("y");
        assertEquals(List.of(1L, 2L), y);
        assertEquals("ok", obj.get("z"));
    }

    @Test
    void parsesEmptyContainers() {
        assertEquals(List.of(), JsonReader.parse("[]"));
        assertEquals(Map.of(), JsonReader.parseObject("{}"));
    }

    @Test
    void parsesWhitespace() {
        Map<String, Object> obj = JsonReader.parseObject("  { \"a\" : 1 , \"b\" : [ 2 ] } ");
        assertEquals(1L, obj.get("a"));
    }

    @Test
    void rejectsMalformed() {
        assertThrows(JsonParseException.class, () -> JsonReader.parse(""));
        assertThrows(JsonParseException.class, () -> JsonReader.parse("{"));
        assertThrows(JsonParseException.class, () -> JsonReader.parse("[1,]"));
        assertThrows(JsonParseException.class, () -> JsonReader.parse("\"unterminated"));
        assertThrows(JsonParseException.class, () -> JsonReader.parse("nul"));
        assertThrows(JsonParseException.class, () -> JsonReader.parse("{\"a\":1}{"));
        assertThrows(JsonParseException.class, () -> JsonReader.parse((String) null));
    }

    @Test
    void rejectsExpectedObject() {
        assertThrows(JsonParseException.class, () -> JsonReader.parseObject("[1]"));
    }

    @Test
    void parsesLongOverflowAsDouble() {
        Object v = JsonReader.parse("99999999999999999999");
        assertTrue(v instanceof Double, "expected Double, got " + v.getClass());
    }
}
