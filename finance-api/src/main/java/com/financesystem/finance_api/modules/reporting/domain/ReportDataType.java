package com.financesystem.finance_api.modules.reporting.domain;

/**
 * Logical data type of a result column / filter parameter. Derived from the
 * view (or the {@code ResultSetMetaData}) so the frontend can chart without
 * guessing. Kept deliberately small.
 */
public enum ReportDataType {
    TEXT,
    NUMBER,
    BOOLEAN,
    DATE,
    TIMESTAMP
}
