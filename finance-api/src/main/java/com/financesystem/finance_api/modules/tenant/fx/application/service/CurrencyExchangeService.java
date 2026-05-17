package com.financesystem.finance_api.modules.tenant.fx.application.service;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.fx.application.dto.TransactionFxDetailResponse;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FeeCalculationMode;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FeeType;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxExchangeRate;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxQuote;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.FxOperationCode;
import com.financesystem.finance_api.modules.tenant.fx.domain.model.OperationFee;
import com.financesystem.finance_api.modules.tenant.fx.domain.repository.FxExchangeRateRepository;
import com.financesystem.finance_api.modules.tenant.fx.domain.repository.OperationFeeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CurrencyExchangeService {

    private static final int AMOUNT_SCALE = 4;
    private static final int MONEY_SCALE = 2;

    private final FxExchangeRateRepository fxExchangeRateRepository;
    private final OperationFeeRepository operationFeeRepository;

    public CurrencyExchangeService(
            FxExchangeRateRepository fxExchangeRateRepository,
            OperationFeeRepository operationFeeRepository
    ) {
        this.fxExchangeRateRepository = fxExchangeRateRepository;
        this.operationFeeRepository = operationFeeRepository;
    }

    public FxQuote calculate(FxOperationCode operationCode, BigDecimal sourceAmount, CurrencyCode sourceCurrency, CurrencyCode targetCurrency) {
        if (operationCode == null) {
            throw new BusinessException("Operation code is required for FX calculation");
        }
        if (sourceAmount == null) {
            throw new BusinessException("FX source amount is required");
        }

        BigDecimal normalizedSourceAmount = normalizeMoney(sourceAmount);
        FxOperationCode feeOperationCode = resolveFeeOperationCode(operationCode, sourceCurrency, targetCurrency);

        if (sourceCurrency == targetCurrency) {
            OperationFee fee = resolveFee(feeOperationCode);
            BigDecimal feeAmount = calculateFee(fee, normalizedSourceAmount);
            BigDecimal netAmount = subtractFee(normalizedSourceAmount, feeAmount);
            BigDecimal effectiveRate = normalizedSourceAmount.signum() == 0
                    ? BigDecimal.ONE.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP)
                    : netAmount.divide(normalizedSourceAmount, AMOUNT_SCALE, RoundingMode.HALF_UP);
            boolean feeIncludedInRate = fee.calculationMode() == FeeCalculationMode.INCLUDED && feeAmount.compareTo(BigDecimal.ZERO) > 0;
            return new FxQuote(
                    false,
                    feeOperationCode,
                    sourceCurrency,
                    targetCurrency,
                    normalizedSourceAmount,
                    normalizedSourceAmount,
                    netAmount,
                    BigDecimal.ONE.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP),
                    feeIncludedInRate ? effectiveRate : BigDecimal.ONE.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP),
                    feeAmount,
                    targetCurrency,
                    fee.feeType(),
                    fee.calculationMode(),
                    feeIncludedInRate
            );
        }

        FxExchangeRate exchangeRate = fxExchangeRateRepository
                .findBySourceCurrencyAndTargetCurrency(sourceCurrency, targetCurrency)
                .orElseThrow(() -> new BusinessException("FX rate not found for pair " + sourceCurrency + " -> " + targetCurrency));

        if (!exchangeRate.active()) {
            throw new BusinessException("FX rate is inactive for pair " + sourceCurrency + " -> " + targetCurrency);
        }

        BigDecimal rate = normalizeRate(exchangeRate.rate());
        BigDecimal targetGross = normalizedSourceAmount.multiply(rate).setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
        OperationFee fee = resolveFee(feeOperationCode);
        BigDecimal feeAmount = calculateFee(fee, targetGross);
        BigDecimal targetNet = subtractFee(targetGross, feeAmount);
        boolean feeIncludedInRate = fee.calculationMode() == FeeCalculationMode.INCLUDED && feeAmount.compareTo(BigDecimal.ZERO) > 0;
        BigDecimal effectiveRate = normalizedSourceAmount.signum() == 0
                ? rate
                : targetNet.divide(normalizedSourceAmount, AMOUNT_SCALE, RoundingMode.HALF_UP);

        return new FxQuote(
                true,
                feeOperationCode,
                sourceCurrency,
                targetCurrency,
                normalizedSourceAmount,
                targetGross,
                targetNet,
                rate,
                feeIncludedInRate ? effectiveRate : rate,
                feeAmount,
                targetCurrency,
                fee.feeType(),
                fee.calculationMode(),
                feeIncludedInRate
        );
    }

    public FxQuote calculateForTargetAmount(FxOperationCode operationCode, BigDecimal targetAmount, CurrencyCode sourceCurrency, CurrencyCode targetCurrency) {
        if (operationCode == null) {
            throw new BusinessException("Operation code is required for FX calculation");
        }
        if (targetAmount == null) {
            throw new BusinessException("FX target amount is required");
        }

        BigDecimal normalizedTargetAmount = normalizeMoney(targetAmount);
        FxOperationCode feeOperationCode = resolveFeeOperationCode(operationCode, sourceCurrency, targetCurrency);

        if (sourceCurrency == targetCurrency) {
            OperationFee fee = resolveFee(feeOperationCode);
            BigDecimal feeAmount = calculateFee(fee, normalizedTargetAmount);
            BigDecimal netAmount = subtractFee(normalizedTargetAmount, feeAmount);
            BigDecimal effectiveRate = normalizedTargetAmount.signum() == 0
                    ? BigDecimal.ONE.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP)
                    : netAmount.divide(normalizedTargetAmount, AMOUNT_SCALE, RoundingMode.HALF_UP);
            boolean feeIncludedInRate = fee.calculationMode() == FeeCalculationMode.INCLUDED && feeAmount.compareTo(BigDecimal.ZERO) > 0;

            return new FxQuote(
                    false,
                    feeOperationCode,
                    sourceCurrency,
                    targetCurrency,
                    normalizedTargetAmount,
                    normalizedTargetAmount,
                    netAmount,
                    BigDecimal.ONE.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP),
                    feeIncludedInRate ? effectiveRate : BigDecimal.ONE.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP),
                    feeAmount,
                    targetCurrency,
                    fee.feeType(),
                    fee.calculationMode(),
                    feeIncludedInRate
            );
        }

        FxExchangeRate exchangeRate = fxExchangeRateRepository
                .findBySourceCurrencyAndTargetCurrency(sourceCurrency, targetCurrency)
                .orElseThrow(() -> new BusinessException("FX rate not found for pair " + sourceCurrency + " -> " + targetCurrency));

        if (!exchangeRate.active()) {
            throw new BusinessException("FX rate is inactive for pair " + sourceCurrency + " -> " + targetCurrency);
        }

        BigDecimal rate = normalizeRate(exchangeRate.rate());
        BigDecimal sourceAmount = normalizedTargetAmount.divide(rate, AMOUNT_SCALE, RoundingMode.HALF_UP);
        OperationFee fee = resolveFee(feeOperationCode);
        BigDecimal feeAmount = calculateFee(fee, normalizedTargetAmount);
        BigDecimal targetNet = subtractFee(normalizedTargetAmount, feeAmount);
        boolean feeIncludedInRate = fee.calculationMode() == FeeCalculationMode.INCLUDED && feeAmount.compareTo(BigDecimal.ZERO) > 0;
        BigDecimal effectiveRate = sourceAmount.signum() == 0
                ? rate
                : targetNet.divide(sourceAmount, AMOUNT_SCALE, RoundingMode.HALF_UP);

        return new FxQuote(
                true,
                feeOperationCode,
                sourceCurrency,
                targetCurrency,
                sourceAmount,
                normalizedTargetAmount,
                targetNet,
                rate,
                feeIncludedInRate ? effectiveRate : rate,
                feeAmount,
                targetCurrency,
                fee.feeType(),
                fee.calculationMode(),
                feeIncludedInRate
        );
    }

    public TransactionFxDetailResponse toResponse(FxQuote quote) {
        if (quote == null) {
            return null;
        }

        return new TransactionFxDetailResponse(
                quote.applied(),
                quote.operationCode(),
                quote.sourceCurrency(),
                quote.targetCurrency(),
                quote.sourceAmount(),
                quote.targetAmountGross(),
                quote.targetAmountNet(),
                quote.exchangeRate(),
                quote.effectiveExchangeRate(),
                quote.feeAmount(),
                quote.feeCurrency(),
                quote.feeType(),
                quote.calculationMode(),
                quote.feeIncludedInRate()
        );
    }

    private OperationFee resolveFee(FxOperationCode operationCode) {
        return operationFeeRepository.findByOperationCode(operationCode)
                .orElse(new OperationFee(null, operationCode, FeeType.NONE, BigDecimal.ZERO, FeeCalculationMode.SEPARATE, true, null, null, null));
    }

    private BigDecimal calculateFee(OperationFee fee, BigDecimal baseAmount) {
        if (fee == null || !fee.active() || fee.feeType() == FeeType.NONE) {
            return BigDecimal.ZERO.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
        }

        return switch (fee.feeType()) {
            case FIXED -> normalizeMoney(fee.feeValue());
            case PERCENTAGE -> baseAmount.multiply(normalizeRate(fee.feeValue())).divide(BigDecimal.valueOf(100), AMOUNT_SCALE, RoundingMode.HALF_UP);
            case NONE -> BigDecimal.ZERO.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
        };
    }

    private BigDecimal subtractFee(BigDecimal gross, BigDecimal feeAmount) {
        BigDecimal result = gross.subtract(feeAmount).setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
        if (result.signum() < 0) {
            throw new BusinessException("FX fee exceeds calculated amount");
        }

        return result;
    }

    private FxOperationCode resolveFeeOperationCode(
            FxOperationCode operationCode,
            CurrencyCode sourceCurrency,
            CurrencyCode targetCurrency
    ) {
        if (sourceCurrency != targetCurrency) {
            return FxOperationCode.CONVERSION;
        }

        return operationCode;
    }

    private BigDecimal normalizeMoney(BigDecimal amount) {
        return amount.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizeRate(BigDecimal rate) {
        if (rate == null) {
            throw new BusinessException("FX rate is required");
        }

        return rate.setScale(8, RoundingMode.HALF_UP);
    }
}
