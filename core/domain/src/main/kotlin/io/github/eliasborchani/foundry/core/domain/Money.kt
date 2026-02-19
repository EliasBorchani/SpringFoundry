package io.github.eliasborchani.foundry.core.domain

import java.math.BigDecimal
import java.util.Currency

/**
 * Immutable value object representing a monetary amount with a currency.
 * Use this as the type for any financial field instead of bare BigDecimal.
 */
data class Money(
    val amount: BigDecimal,
    val currency: Currency,
) {
    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Cannot add amounts in different currencies" }
        return copy(amount = amount + other.amount)
    }

    override fun toString(): String = "$amount ${currency.currencyCode}"

    companion object {
        fun of(amount: BigDecimal, currencyCode: String): Money =
            Money(amount, Currency.getInstance(currencyCode))

        fun zero(currencyCode: String): Money =
            Money(BigDecimal.ZERO, Currency.getInstance(currencyCode))
    }
}
