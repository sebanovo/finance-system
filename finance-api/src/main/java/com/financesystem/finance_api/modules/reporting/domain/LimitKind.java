package com.financesystem.finance_api.modules.reporting.domain;

/**
 * Where the LIMIT on an executed query came from.
 *
 * <ul>
 *   <li>{@link #USER}: the SQL already carried a LIMIT (user/report intent, e.g.
 *       a "Top 10"). A full export must NOT widen it.</li>
 *   <li>{@link #SYSTEM}: the guard injected the default {@code AI_SQL_MAX_ROWS}.
 *       A full export may replace it with the larger export cap.</li>
 * </ul>
 */
public enum LimitKind {
    USER,
    SYSTEM
}
