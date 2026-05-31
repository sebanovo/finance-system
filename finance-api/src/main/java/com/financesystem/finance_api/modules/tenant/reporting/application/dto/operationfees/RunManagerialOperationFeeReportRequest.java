package com.financesystem.finance_api.modules.tenant.reporting.application.dto.operationfees;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportVisualizationType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.operationfees.OperationFeeReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.operationfees.OperationFeeReportMetricKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RunManagerialOperationFeeReportRequest(
        @NotNull ReportType reportType,
        @NotEmpty List<OperationFeeReportGroupByKey> groupBy,
        @NotEmpty List<OperationFeeReportMetricKey> metrics,
        List<@Valid OperationFeeReportFilterRequest> filters,
        List<@Valid OperationFeeReportManagerialSortRequest> sort,
        @NotNull @Valid ReportPaginationRequest pagination,
        @NotEmpty List<ReportOutput> outputs,
        List<ReportVisualizationType> visualizations
) {
}
