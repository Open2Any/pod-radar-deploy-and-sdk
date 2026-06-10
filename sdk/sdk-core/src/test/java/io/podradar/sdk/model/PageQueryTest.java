package io.podradar.sdk.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageQueryTest {

    @Test
    void ofConstructsAndAccesses() {
        PageQuery q = PageQuery.of(50, 100);
        assertEquals(50, q.limit());
        assertEquals(100, q.offset());
    }

    @Test
    void nextAdvancesByLimit() {
        PageQuery q = PageQuery.of(50, 100).next();
        assertEquals(50, q.limit());
        assertEquals(150, q.offset());
    }

    @Test
    void rejectsBadLimit() {
        assertThrows(IllegalArgumentException.class, () -> PageQuery.of(0, 0));
        assertThrows(IllegalArgumentException.class, () -> PageQuery.of(1001, 0));
        assertThrows(IllegalArgumentException.class, () -> PageQuery.of(-1, 0));
    }

    @Test
    void rejectsNegativeOffset() {
        assertThrows(IllegalArgumentException.class, () -> PageQuery.of(50, -1));
    }

    @Test
    void equalsAndHashCode() {
        assertEquals(PageQuery.of(50, 100), PageQuery.of(50, 100));
        assertNotEquals(PageQuery.of(50, 100), PageQuery.of(50, 0));
        assertEquals(PageQuery.of(50, 100).hashCode(), PageQuery.of(50, 100).hashCode());
    }
}
