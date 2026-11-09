package com.prestige.renovation.entity;

/**
 * Explicit PostgreSQL column types used in {@code @Column(columnDefinition = ...)} so that
 * Hibernate's {@code ddl-auto=validate} check matches the Flyway schema exactly.
 */
public final class Columns {
    public static final String TIMESTAMPTZ = "timestamptz";
    public static final String BOOLEAN = "boolean";
    public static final String TEXT = "text";
    public static final String BYTEA = "bytea";

    private Columns() {
    }
}
