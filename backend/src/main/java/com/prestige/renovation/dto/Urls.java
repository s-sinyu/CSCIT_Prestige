package com.prestige.renovation.dto;

/** Builds API-relative URLs for stored files, e.g. {@code /api/files/<key>}. */
public final class Urls {

    private Urls() {
    }

    public static String file(String key) {
        return key == null ? null : "/api/files/" + key;
    }
}
