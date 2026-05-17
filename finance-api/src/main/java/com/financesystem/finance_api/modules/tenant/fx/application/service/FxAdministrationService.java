package com.financesystem.finance_api.modules.tenant.fx.application.service;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.fx.application.dto.*;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FeeCalculationMode;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FeeType;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxExchangeRate;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxOperationCode;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.OperationFee;
import com.financesystem.finance_api.modules.tenant.fx.domain.repository.FxExchangeRateRepository;
import com.financesystem.finance_api.modules.tenant.fx.domain.repository.OperationFeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class FxAdministrationService {

    private final FxExchangeRateRepository fxExchangeRateRepository;
    private final OperationFeeRepository operationFeeRepository;

    public FxAdministrationService(
            FxExchangeRateRepository fxExchangeRateRepository,
            OperationFeeRepository operationFeeRepository
    ) {
        this.fxExchangeRateRepository = fxExchangeRateRepository;
        this.operationFeeRepository = operationFeeRepository;
    }

    @Transactional(readOnly = true)
    public List<FxExchangeRateResponse> listRates() {
        return fxExchangeRateRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FxExchangeRateResponse getRate(UUID id) {
        return toResponse(findRateById(id));
    }

    @Transactional
    public FxExchangeRateResponse createRate(CreateFxExchangeRateRequest request) {
        CurrencyCode sourceCurrency = request.sourceCurrency();
        CurrencyCode targetCurrency = request.targetCurrency();
        BigDecimal rate = normalizeRate(request.rate());
        validateRatePair(sourceCurrency, targetCurrency, rate);
        ensureRatePairAvailable(null, sourceCurrency, targetCurrency);

        FxExchangeRate saved = fxExchangeRateRepository.save(new FxExchangeRate(
                null,
                sourceCurrency,
                targetCurrency,
                rate,
                request.active(),
                normalizeDescription(request.description()),
                null,
                null
        ));
        return toResponse(saved);
    }

    @Transactional
    public FxExchangeRateResponse updateRate(UUID id, UpdateFxExchangeRateRequest request) {
        FxExchangeRate existing = findRateById(id);
        CurrencyCode sourceCurrency = request.sourceCurrency();
        CurrencyCode targetCurrency = request.targetCurrency();
        BigDecimal rate = normalizeRate(request.rate());
        validateRatePair(sourceCurrency, targetCurrency, rate);
        ensureRatePairAvailable(id, sourceCurrency, targetCurrency);

        FxExchangeRate saved = fxExchangeRateRepository.save(new FxExchangeRate(
                existing.id(),
                sourceCurrency,
                targetCurrency,
                rate,
                request.active(),
                normalizeDescription(request.description()),
                existing.createdAt(),
                existing.updatedAt()
        ));
        return toResponse(saved);
    }

    @Transactional
    public void deleteRate(UUID id) {
        findRateById(id);
        fxExchangeRateRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<OperationFeeResponse> listFees() {
        return operationFeeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OperationFeeResponse getFee(UUID id) {
        return toResponse(findFeeById(id));
    }

    @Transactional
    public OperationFeeResponse createFee(CreateOperationFeeRequest request) {
        FxOperationCode operationCode = request.operationCode();
        ensureFeeOperationAvailable(null, operationCode);
        OperationFee saved = operationFeeRepository.save(buildOperationFee(null, request, operationCode, null, null));
        return toResponse(saved);
    }

    @Transactional
    public OperationFeeResponse updateFee(UUID id, UpdateOperationFeeRequest request) {
        OperationFee existing = findFeeById(id);
        FxOperationCode operationCode = request.operationCode();
        ensureFeeOperationAvailable(id, operationCode);
        OperationFee saved = operationFeeRepository.save(buildOperationFee(existing.id(), request, operationCode, existing.createdAt(), existing.updatedAt()));
        return toResponse(saved);
    }

    @Transactional
    public void deleteFee(UUID id) {
        findFeeById(id);
        operationFeeRepository.deleteById(id);
    }

    private OperationFee buildOperationFee(
            UUID id,
            CreateOperationFeeRequest request,
            FxOperationCode operationCode,
            java.time.Instant createdAt,
            java.time.Instant updatedAt
    ) {
        return buildOperationFee(
                id,
                request.feeType(),
                request.feeValue(),
                request.calculationMode(),
                request.active(),
                normalizeDescription(request.description()),
                operationCode,
                createdAt,
                updatedAt
        );
    }

    private OperationFee buildOperationFee(
            UUID id,
            UpdateOperationFeeRequest request,
            FxOperationCode operationCode,
            java.time.Instant createdAt,
            java.time.Instant updatedAt
    ) {
        return buildOperationFee(
                id,
                request.feeType(),
                request.feeValue(),
                request.calculationMode(),
                request.active(),
                normalizeDescription(request.description()),
                operationCode,
                createdAt,
                updatedAt
        );
    }

    private OperationFee buildOperationFee(
            UUID id,
            FeeType feeType,
            BigDecimal feeValue,
            FeeCalculationMode calculationMode,
            boolean active,
            String description,
            FxOperationCode operationCode,
            java.time.Instant createdAt,
            java.time.Instant updatedAt
    ) {
        BigDecimal normalizedFeeValue = normalizeFeeValue(feeType, feeValue);
        return new OperationFee(
                id,
                operationCode,
                feeType,
                normalizedFeeValue,
                calculationMode,
                active,
                description,
                createdAt,
                updatedAt
        );
    }

    private FxExchangeRate findRateById(UUID id) {
        if (id == null) {
            throw new BusinessException("FX rate id is required");
        }

        return fxExchangeRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FX rate not found with id: " + id));
    }

    private OperationFee findFeeById(UUID id) {
        if (id == null) {
            throw new BusinessException("FX fee id is required");
        }

        return operationFeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FX fee not found with id: " + id));
    }

    private void ensureRatePairAvailable(UUID currentId, CurrencyCode sourceCurrency, CurrencyCode targetCurrency) {
        fxExchangeRateRepository.findBySourceCurrencyAndTargetCurrency(sourceCurrency, targetCurrency)
                .filter(rate -> currentId == null || !currentId.equals(rate.id()))
                .ifPresent(rate -> {
                    throw new BusinessException("FX rate already exists for pair " + sourceCurrency + " -> " + targetCurrency);
                });
    }

    private void ensureFeeOperationAvailable(UUID currentId, FxOperationCode operationCode) {
        operationFeeRepository.findByOperationCode(operationCode)
                .filter(fee -> currentId == null || !currentId.equals(fee.id()))
                .ifPresent(fee -> {
                    throw new BusinessException("FX fee already exists for operation " + operationCode);
                });
    }

    private void validateRatePair(CurrencyCode sourceCurrency, CurrencyCode targetCurrency, BigDecimal rate) {
        if (sourceCurrency.equals(targetCurrency) && rate.compareTo(BigDecimal.ONE) != 0) {
            throw new BusinessException("Self exchange rate must be 1");
        }

        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("FX rate must be greater than zero");
        }
    }

    private BigDecimal normalizeRate(BigDecimal rate) {
        if (rate == null) {
            throw new BusinessException("FX rate is required");
        }

        return rate.setScale(8, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal normalizeFeeValue(FeeType feeType, BigDecimal feeValue) {
        if (feeType == null) {
            throw new BusinessException("Fee type is required");
        }

        if (feeValue == null) {
            throw new BusinessException("Fee value is required");
        }

        BigDecimal normalized = feeValue.setScale(8, java.math.RoundingMode.HALF_UP);
        if (normalized.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Fee value cannot be negative");
        }

        if (feeType == FeeType.NONE && normalized.compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException("Fee value must be zero when fee type is NONE");
        }

        return normalized;
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }

        String trimmed = description.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private FxExchangeRateResponse toResponse(FxExchangeRate rate) {
        return new FxExchangeRateResponse(
                rate.id(),
                rate.sourceCurrency(),
                rate.targetCurrency(),
                rate.rate(),
                rate.active(),
                rate.description(),
                rate.createdAt(),
                rate.updatedAt()
        );
    }

    private OperationFeeResponse toResponse(OperationFee fee) {
        return new OperationFeeResponse(
                fee.id(),
                fee.operationCode(),
                fee.feeType(),
                fee.feeValue(),
                fee.calculationMode(),
                fee.active(),
                fee.description(),
                fee.createdAt(),
                fee.updatedAt()
        );
    }
}
