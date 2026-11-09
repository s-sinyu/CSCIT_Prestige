package com.prestige.renovation.common.util;

import java.util.List;

/**
 * Minimal RFC 4180 CSV builder (FR-PM-8). Fields containing commas, quotes or line breaks are quoted
 * and quotes doubled. Cells starting with = + - @ (or tab / carriage return) are prefixed with a single quote
 * to prevent CSV formula injection when the file is opened in Excel.
 */
public class CsvWriter {

    private final StringBuilder sb = new StringBuilder();

    public CsvWriter row(Object... cells) {
        return row(List.of(cells));
    }

    public CsvWriter row(List<?> cells) {
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(escape(cells.get(i)));
        }
        sb.append("\r\n");
        return this;
    }

    public CsvWriter blankLine() {
        sb.append("\r\n");
        return this;
    }

    /** UTF-8 byte order mark so Excel detects the encoding. */
    public String build() {
        return "﻿" + sb;
    }

    public static String escape(Object value) {
        if (value == null) {
            return "";
        }
        String s = value.toString();
        if (!s.isEmpty() && "=+-@\t\r".indexOf(s.charAt(0)) >= 0 && !(value instanceof Number)) {
            s = "'" + s;
        }
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            s = "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}
