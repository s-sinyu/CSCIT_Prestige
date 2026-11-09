package com.prestige.renovation.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.prestige.renovation.common.util.CsvWriter;

/** CSV escaping and formula-injection guard (FR-PM-8). */
class CsvWriterTest {

    @Test
    void plainValuesAreNotQuoted() {
        assertThat(CsvWriter.escape("Kitchen")).isEqualTo("Kitchen");
        assertThat(CsvWriter.escape(42)).isEqualTo("42");
        assertThat(CsvWriter.escape(null)).isEmpty();
    }

    @Test
    void commasQuotesAndNewlinesAreQuoted() {
        assertThat(CsvWriter.escape("Tan, Lim & Co")).isEqualTo("\"Tan, Lim & Co\"");
        assertThat(CsvWriter.escape("The \"Loft\"")).isEqualTo("\"The \"\"Loft\"\"\"");
        assertThat(CsvWriter.escape("line1\nline2")).isEqualTo("\"line1\nline2\"");
    }

    @Test
    void formulaPrefixesAreNeutralised() {
        assertThat(CsvWriter.escape("=HYPERLINK(\"x\")")).isEqualTo("\"'=HYPERLINK(\"\"x\"\")\"");
        assertThat(CsvWriter.escape("+65 9123")).isEqualTo("'+65 9123");
        assertThat(CsvWriter.escape("-cmd")).isEqualTo("'-cmd");
        assertThat(CsvWriter.escape("@SUM(A1)")).isEqualTo("'@SUM(A1)");
    }

    @Test
    void negativeNumbersAreLeftAlone() {
        assertThat(CsvWriter.escape(-5)).isEqualTo("-5");
    }

    @Test
    void buildAddsByteOrderMarkAndCrLf() {
        String csv = new CsvWriter().row("a", "b").build();
        assertThat(csv).isEqualTo("﻿a,b\r\n");
    }
}
