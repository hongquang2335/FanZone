package com.example.myapplication.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersTest {
    @Test
    fun formatVnd_usesVietnameseCurrencySuffixAndDotGrouping() {
        assertEquals("2.500.000\u0111", formatVnd(2_500_000))
    }
}
