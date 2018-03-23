package com.commercetools.bulkpricer;

import org.javamoney.moneta.internal.DefaultRoundingProvider;
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryRounding;
import javax.money.RoundingQueryBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isEmpty;

final class MoneyRepresentation {
  private final Integer centAmount;
  private final String currencyCode;

  private static final DefaultRoundingProvider ROUNDING_PROVIDER = new DefaultRoundingProvider();

  private MoneyRepresentation(final Integer centAmount, final String currencyCode) {
    this.centAmount = centAmount;
    this.currencyCode = currencyCode;
  }

  /**
   * Creates a new Money instance.
   * Money can't represent cent fractions. The value will be rounded to nearest cent value using RoundingMode.HALF_EVEN.
   * @param monetaryAmount the amount with currency to transform
   */
  public MoneyRepresentation(final MonetaryAmount monetaryAmount) {
    this(amountToCents(monetaryAmount), requireValidCurrencyCode(monetaryAmount.getCurrency().getCurrencyCode()));
  }

  public int getCentAmount() {
    return centAmount.intValue();
  }

  /**
   * @return The ISO 4217 currency code, for example "EUR" or "USD".
   */
  public String getCurrencyCode() {
    return currencyCode;
  }

  private static String requireValidCurrencyCode(final String currencyCode) {
    if (isEmpty(currencyCode))
      throw new IllegalArgumentException("Money.currencyCode can't be empty.");
    return currencyCode;
  }

  public static Integer amountToCents(final MonetaryAmount monetaryAmount) {
    final MonetaryRounding ROUNDING =
      ROUNDING_PROVIDER.getRounding(RoundingQueryBuilder.of().setRoundingName("default").setCurrency(monetaryAmount.getCurrency())
        .build());
    return monetaryAmount
      .with(ROUNDING)
      .query(MoneyRepresentation::queryFrom);
  }

  private static Integer queryFrom(MonetaryAmount amount) {
    Objects.requireNonNull(amount, "Amount required.");
    BigDecimal number = amount.getNumber().numberValue(BigDecimal.class);
    CurrencyUnit cur = amount.getCurrency();
    int scale = cur.getDefaultFractionDigits();
    if(scale<0){
      scale = 0;
    }
    number = number.setScale(scale, RoundingMode.DOWN);
    return number.movePointRight(number.scale()).intValueExact();
  }
}
