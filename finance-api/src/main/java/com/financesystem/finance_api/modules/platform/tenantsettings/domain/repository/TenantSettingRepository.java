package com.financesystem.finance_api.modules.platform.tenantsettings.domain.repository;

import com.financesystem.finance_api.modules.platform.tenantsettings.domain.model.TenantSetting;

import java.util.List;
import java.util.Optional;

public interface TenantSettingRepository {

    List<TenantSetting> findAll();

    Optional<TenantSetting> findByKey(String settingKey);

    TenantSetting save(TenantSetting tenantSetting);
}