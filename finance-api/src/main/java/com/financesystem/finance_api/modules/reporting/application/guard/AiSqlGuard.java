package com.financesystem.finance_api.modules.reporting.application.guard;

import com.financesystem.finance_api.modules.reporting.domain.LimitKind;
import com.financesystem.finance_api.modules.reporting.domain.ReportScope;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validates SQL — whether produced by the LLM or by a controlled
 * {@code sqlTemplate} — before it is ever executed.
 *
 * <p>The whole SQL tree is inspected (FROM, JOINs, subqueries, CTEs/WITH,
 * UNIONs and function arguments via {@link TablesNamesFinder}); a dangerous
 * reference cannot hide inside {@code WITH x AS (SELECT ... information_schema ...)}
 * or {@code WHERE id IN (SELECT ... FROM password_reset_tokens)}.
 *
 * <p>Rules:
 * <ul>
 *   <li>exactly one statement, and it must be a {@code SELECT} / {@code WITH … SELECT};</li>
 *   <li>no multi-statement, no DML/DDL, no {@code SELECT … INTO};</li>
 *   <li>no prohibited functions ({@code pg_sleep}, {@code pg_read_file}, {@code dblink}, …);</li>
 *   <li>no {@code pg_catalog} / {@code information_schema} (and no other schema in TENANT scope);</li>
 *   <li>every referenced table must be a whitelisted {@code reporting_*} view (CTE names excepted);</li>
 *   <li>a LIMIT is guaranteed: respect a user LIMIT, otherwise inject the system one.</li>
 * </ul>
 */
@Component
public class AiSqlGuard {

    /** Prohibited functions / dangerous tokens. Defense in depth on top of the structural checks. */
    private static final List<String> PROHIBITED_TOKENS = List.of(
            "pg_sleep", "pg_read_file", "pg_read_binary_file", "pg_ls_dir", "pg_stat_file",
            "lo_import", "lo_export", "dblink", "set_config", "current_setting",
            "pg_terminate_backend", "pg_cancel_backend", "pg_reload_conf",
            "pg_catalog", "information_schema"
    );

    private static final Pattern SELECT_INTO = Pattern.compile("\\binto\\b", Pattern.CASE_INSENSITIVE);

    public GuardedSql guard(String rawSql, ReportScope scope, Set<String> allowedViews,
                            String scopeSchema, int systemLimit) {
        if (rawSql == null || rawSql.isBlank()) {
            throw new SqlGuardException("El SQL está vacío.");
        }
        String sql = rawSql.trim();
        String lower = sql.toLowerCase(Locale.ROOT);

        // 1. Token denylist (fail closed — also blocks dangerous strings).
        for (String token : PROHIBITED_TOKENS) {
            if (lower.contains(token)) {
                throw new SqlGuardException("Referencia o función prohibida: " + token);
            }
        }

        // 2. Exactly one statement, and it must be a SELECT.
        Statements statements;
        try {
            statements = CCJSqlParserUtil.parseStatements(sql);
        } catch (Exception e) {
            throw new SqlGuardException("SQL no parseable.");
        }
        if (statements.getStatements().size() != 1) {
            throw new SqlGuardException("Solo se permite una sentencia.");
        }
        Statement statement = statements.getStatements().get(0);
        if (!(statement instanceof Select select)) {
            throw new SqlGuardException("Solo se permiten consultas SELECT.");
        }

        // 3. No SELECT ... INTO (would create a table).
        if (select instanceof PlainSelect ps && ps.getIntoTables() != null && !ps.getIntoTables().isEmpty()) {
            throw new SqlGuardException("SELECT INTO no permitido.");
        }
        if (SELECT_INTO.matcher(lower).find()) {
            throw new SqlGuardException("Cláusula INTO no permitida.");
        }

        // 4. Collect CTE names (allowed internal aliases, excluded from the whitelist check).
        Set<String> cteNames = collectCteNames(select);

        // 5. Walk the whole tree and validate every referenced table.
        Set<String> referenced;
        try {
            referenced = new TablesNamesFinder().getTables((Statement) select);
        } catch (Exception e) {
            throw new SqlGuardException("No se pudo analizar el árbol SQL.");
        }

        Set<String> usedViews = new LinkedHashSet<>();
        for (String raw : referenced) {
            String name = normalize(raw);
            if (cteNames.contains(name)) {
                continue; // internal CTE reference
            }
            String[] parts = name.split("\\.");
            if (parts.length == 1) {
                requireWhitelisted(parts[0], allowedViews);
                usedViews.add(parts[0]);
            } else if (parts.length == 2) {
                if (scope == ReportScope.TENANT) {
                    throw new SqlGuardException("En scope TENANT no se permiten referencias con schema: " + name);
                }
                if (!"reporting".equals(parts[0])) {
                    throw new SqlGuardException("Schema no permitido: " + parts[0]);
                }
                requireWhitelisted(parts[1], allowedViews);
                usedViews.add(parts[1]);
            } else {
                throw new SqlGuardException("Referencia totalmente cualificada no permitida: " + name);
            }
        }

        if (usedViews.isEmpty()) {
            throw new SqlGuardException("La consulta no referencia ninguna vista reporting_*.");
        }

        // 6. LIMIT: respect a user LIMIT, otherwise inject the system one.
        boolean hasUserLimit = select.getLimit() != null && select.getLimit().getRowCount() != null;
        String safeSql;
        LimitKind limitKind;
        if (hasUserLimit) {
            safeSql = select.toString();
            limitKind = LimitKind.USER;
        } else {
            safeSql = select.toString() + " LIMIT " + systemLimit;
            limitKind = LimitKind.SYSTEM;
        }

        return new GuardedSql(safeSql, new ArrayList<>(usedViews), scopeSchema, limitKind);
    }

    private Set<String> collectCteNames(Select select) {
        Set<String> names = new LinkedHashSet<>();
        List<WithItem> withItems = select.getWithItemsList();
        if (withItems != null) {
            for (WithItem item : withItems) {
                if (item.getAlias() != null && item.getAlias().getName() != null) {
                    names.add(normalize(item.getAlias().getName()));
                }
            }
        }
        return names;
    }

    private void requireWhitelisted(String view, Set<String> allowedViews) {
        for (String allowed : allowedViews) {
            if (allowed.equalsIgnoreCase(view)) {
                return;
            }
        }
        throw new SqlGuardException("Vista no permitida: " + view);
    }

    /** Lowercase and strip surrounding quotes/backticks so comparisons are stable. */
    private String normalize(String identifier) {
        String n = identifier.trim().toLowerCase(Locale.ROOT);
        n = n.replace("\"", "").replace("`", "");
        return n;
    }
}
