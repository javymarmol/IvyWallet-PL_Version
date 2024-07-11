package com.ivy.exchangeRates

import com.ivy.core.persistence.algorithm.calc.Rate
import com.ivy.core.persistence.algorithm.calc.RatesDao
import com.ivy.data.CurrencyCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class RatesDaoFake: RatesDao {

    private val rates = MutableStateFlow<List<Rate>>(
        listOf(
            Rate(currency = "USD", rate = 1.0),
            Rate(currency = "GBP", rate =  0.7),
            Rate(currency = "CAD", rate = 1.5),
            Rate(currency = "AUD", rate = 1.2),
            Rate(currency = "JPY", rate = 120.0),
        )
    )

    private val ratesOverrides = MutableStateFlow<List<Rate>>(
        listOf(
            Rate(currency = "CAD", rate = 1.3),
        )
    )

    override fun findAll(baseCurrency: CurrencyCode): Flow<List<Rate>> {
        return rates
    }

    override fun findAllOverrides(baseCurrency: CurrencyCode): Flow<List<Rate>> {
        return ratesOverrides
    }
}