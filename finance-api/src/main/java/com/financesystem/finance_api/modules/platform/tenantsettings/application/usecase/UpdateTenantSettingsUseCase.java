package com.financesystem.finance_api.modules.platform.tenantsettings.application.usecase;

import com.financesystem.finance_api.modules.platform.tenantsettings.application.dto.TenantSettingsResponse;
import com.financesystem.finance_api.modules.platform.tenantsettings.application.dto.UpdateTenantSettingsRequest;
import com.financesystem.finance_api.modules.platform.tenantsettings.application.mapper.TenantSettingsMapper;
import com.financesystem.finance_api.modules.platform.tenantsettings.domain.model.TenantSetting;
import com.financesystem.finance_api.modules.platform.tenantsettings.domain.model.TenantSettingKeys;
import com.financesystem.finance_api.modules.platform.tenantsettings.domain.repository.TenantSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateTenantSettingsUseCase {

    private final TenantSettingRepository tenantSettingRepository;
    private final TenantSettingsMapper tenantSettingsMapper;

    public UpdateTenantSettingsUseCase(
            TenantSettingRepository tenantSettingRepository,
            TenantSettingsMapper tenantSettingsMapper
    ) {
        this.tenantSettingRepository = tenantSettingRepository;
        this.tenantSettingsMapper = tenantSettingsMapper;
    }

    @Transactional
    public TenantSettingsResponse execute(UpdateTenantSettingsRequest request) {
        saveOrUpdate(TenantSettingKeys.COMPANY_NAME, normalizeRequired(request.companyName()));
        saveOrUpdate(TenantSettingKeys.COMPANY_LEGAL_NAME, normalizeNullable(request.legalName()));
        saveOrUpdate(TenantSettingKeys.COMPANY_TIMEZONE, normalizeRequired(request.timezone()));
        saveOrUpdate(TenantSettingKeys.COMPANY_CURRENCY, normalizeRequired(request.currency()).toUpperCase());
        saveOrUpdate(TenantSettingKeys.COMPANY_CONTACT_EMAIL, normalizeNullable(request.contactEmail()));
        saveOrUpdate(TenantSettingKeys.COMPANY_CONTACT_PHONE, normalizeNullable(request.contactPhone()));

        return tenantSettingsMapper.toResponse(tenantSettingRepository.findAll());
    }

    private void saveOrUpdate(String key, String value) {
        TenantSetting existing = tenantSettingRepository.findByKey(key).orElse(null);

        TenantSetting settingToSave = new TenantSetting(
                existing != null ? existing.id() : null,
                key,
                value,
                existing != null ? existing.createdAt() : null,
                existing != null ? existing.updatedAt() : null
        );

        tenantSettingRepository.save(settingToSave);
    }

    private String normalizeRequired(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}