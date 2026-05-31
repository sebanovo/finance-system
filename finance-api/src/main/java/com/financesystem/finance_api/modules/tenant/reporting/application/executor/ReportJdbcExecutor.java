package com.financesystem.finance_api.modules.tenant.reporting.application.executor;

import com.financesystem.finance_api.modules.tenant.reporting.application.query.ReportSqlQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Component
public class ReportJdbcExecutor {

    private final JdbcTemplate jdbcTemplate;

    public ReportJdbcExecutor(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Map<String, Object>> execute(ReportSqlQuery query) {
        return jdbcTemplate.queryForList(query.sql(), query.parameters().toArray());
    }
}
