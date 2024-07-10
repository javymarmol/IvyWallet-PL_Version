package com.ivy.core.domain.action.exchange

import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SyncExchangeRatesActTest {

    private lateinit var syncExchangeRatesAct: SyncExchangeRatesAct
    private lateinit var exchangeProviderFake: RemoteExchangeProviderFake
    private lateinit var exchangeRateDaoFake: ExchangeRateDaoFake

    @BeforeEach
    fun setUp() {
        exchangeProviderFake = RemoteExchangeProviderFake()
        exchangeRateDaoFake = ExchangeRateDaoFake()
        syncExchangeRatesAct = SyncExchangeRatesAct(
            exchangeProvider = exchangeProviderFake,
            exchangeRateDao = exchangeRateDaoFake
        )
    }

    @Test
    fun `Test sync exchange rates, negative values ignored`() = runBlocking {
        syncExchangeRatesAct("USD")
        val usdRates = exchangeRateDaoFake
            .findAllByBaseCurrency("USD")
            .first { it.isNotEmpty() }

        val cadRates = usdRates.find { it.currency == "CAD" }

        assertThat(cadRates).isNull()
    }

    @Test
    fun `Test sync exchange rates, valid values saved`() = runBlocking<Unit> {
        syncExchangeRatesAct("USD")
        val usdRates = exchangeRateDaoFake
            .findAllByBaseCurrency("USD")
            .first { it.isNotEmpty() }

        val eurRates = usdRates.find { it.currency == "EUR" }
        val audRates = usdRates.find { it.currency == "AUD" }

        assertThat(eurRates).isNotNull()
        assertThat(audRates).isNotNull()

    }

}