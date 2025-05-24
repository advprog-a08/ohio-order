package id.ac.ui.cs.advprog.ohioorder.meja.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MejaConfigTest {

    private MejaConfig config;

    @BeforeEach
    void setUp() {
        config = MejaConfig.getInstance();
    }

    @Test
    void testGetInstanceReturnsSameInstance() {
        // Test singleton pattern
        MejaConfig instance1 = MejaConfig.getInstance();
        MejaConfig instance2 = MejaConfig.getInstance();

        assertSame(instance1, instance2);
        assertNotNull(instance1);
    }

    @Test
    void testGetMaxTableCount() {
        assertEquals(100, config.getMaxTableCount());
    }

    @Test
    void testGetTablePrefix() {
        assertEquals("A", config.getTablePrefix());
    }

    @Test
    void testGenerateTableNumberSuccess() {
        assertEquals("A1", config.generateTableNumber(1));
        assertEquals("A10", config.generateTableNumber(10));
        assertEquals("A99", config.generateTableNumber(99));
        assertEquals("A100", config.generateTableNumber(100));
    }

    @Test
    void testIsValidTableNumberWithValidFormats() {
        assertTrue(config.isValidTableNumber("A1"));
        assertTrue(config.isValidTableNumber("B25"));
        assertTrue(config.isValidTableNumber("C100"));
        assertTrue(config.isValidTableNumber("Z99"));
        assertTrue(config.isValidTableNumber("M50"));
    }

    @Test
    void testIsValidTableNumberWithValidRangeNumbers() {
        assertTrue(config.isValidTableNumber("A1"));
        assertTrue(config.isValidTableNumber("A100"));
        assertTrue(config.isValidTableNumber("B50"));
    }

    @Test
    void testIsValidTableNumberWithInvalidFormats() {
        assertFalse(config.isValidTableNumber("a1"));
        assertFalse(config.isValidTableNumber("AB1"));
        assertFalse(config.isValidTableNumber("A"));
        assertFalse(config.isValidTableNumber("1A"));
        assertFalse(config.isValidTableNumber("A1B"));
        assertFalse(config.isValidTableNumber("A-1"));  // Negative table number
        assertFalse(config.isValidTableNumber("A 1"));
        assertFalse(config.isValidTableNumber(""));
        assertFalse(config.isValidTableNumber(null));
    }

    @Test
    void testIsValidTableNumberWithInvalidRanges() {
        assertFalse(config.isValidTableNumber("A0"));
        assertFalse(config.isValidTableNumber("A101"));
        assertFalse(config.isValidTableNumber("B0"));
        assertFalse(config.isValidTableNumber("Z101"));
    }

    @Test
    void testIsValidTableNumberWithNumberFormatException() {
        String veryLongNumber = "A" + "9".repeat(15);
        assertFalse(config.isValidTableNumber(veryLongNumber));

        String extremelyLongNumber = "A" + "1".repeat(50);
        assertFalse(config.isValidTableNumber(extremelyLongNumber));
    }

    @Test
    void testIsValidTableNumberEdgeCases() {
        assertTrue(config.isValidTableNumber("A01"));
        assertTrue(config.isValidTableNumber("A001"));
        assertFalse(config.isValidTableNumber("A0"));
    }

    @Test
    void testIsValidTableNumberWithLeadingZeros() {
        assertTrue(config.isValidTableNumber("A01"));
        assertTrue(config.isValidTableNumber("A099"));
        assertFalse(config.isValidTableNumber("A0"));
        assertFalse(config.isValidTableNumber("A00"));
    }
}