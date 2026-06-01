package com.financesystem.finance_api.modules.tenant.reporting.application.usecase;

import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportExportContentUnavailableException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportExportNotFoundException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportExport;
import com.financesystem.finance_api.modules.tenant.reporting.domain.repository.ReportExecutionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DownloadReportExportUseCase {

    private final ReportExecutionRepository reportExecutionRepository;

    public DownloadReportExportUseCase(ReportExecutionRepository reportExecutionRepository) {
        this.reportExecutionRepository = reportExecutionRepository;
    }

    public ReportExport execute(UUID exportId) {
        ReportExport export = reportExecutionRepository.findExportById(exportId)
                .orElseThrow(() -> new ReportExportNotFoundException(exportId));

        if (export.fileContent() == null || export.fileContent().length == 0) {
            throw new ReportExportContentUnavailableException(exportId);
        }

        return export;
    }
}
