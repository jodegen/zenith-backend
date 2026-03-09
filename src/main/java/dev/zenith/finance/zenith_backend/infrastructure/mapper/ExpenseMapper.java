package dev.zenith.finance.zenith_backend.infrastructure.mapper;

import dev.zenith.finance.zenith_backend.domain.model.Expense;
import dev.zenith.finance.zenith_backend.domain.model.Money;
import dev.zenith.finance.zenith_backend.infrastructure.persistence.entity.ExpenseJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.Currency;

@Mapper(componentModel = "spring", uses = {UserAccountMapper.class, CategoryMapper.class})
public interface ExpenseMapper {

    @Mapping(target = "amount", expression = "java(toMoney(entity.getAmount(), entity.getCurrency()))")
    Expense toDomain(ExpenseJpaEntity entity);

    @Mapping(target = "amount", source = "amount.amount")
    @Mapping(target = "currency", source = "amount.currency", qualifiedByName = "currencyToCode")
    ExpenseJpaEntity toEntity(Expense domain);

    default Money toMoney(BigDecimal amount, String currencyCode) {
        return Money.of(amount, currencyCode);
    }

    @Named("currencyToCode")
    default String currencyToCode(Currency currency) {
        return currency != null ? currency.getCurrencyCode() : null;
    }
}

