package com.ivy.exchangeRates

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.ivy.MainCoroutineExtension
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.exchangeRates.data.RateUi
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineExtension::class)
class RatesStateFlowTest {
    private lateinit var baseCurrencyFlow: BaseCurrencyFlow
    private lateinit var ratesDao: RatesDaoFake
    private lateinit var ratesStateFlow: RatesStateFlow

    @BeforeEach
    fun setUp() {
        baseCurrencyFlow = mockk()

        every { baseCurrencyFlow.invoke() } returns flowOf("", "EUR")

        ratesDao = RatesDaoFake()

        ratesStateFlow = RatesStateFlow(
            baseCurrencyFlow = baseCurrencyFlow,
            ratesDao = ratesDao
        )
    }

    @Test
    fun `Test rates flow emissions`() = runTest {
        ratesStateFlow().test {
            val state1 = awaitItem() // Initial emission, ignore
            assertThat(state1.baseCurrency).isEqualTo("")
            assertThat(state1.manual).isEmpty()
            assertThat(state1.automatic).isEmpty()

            val overrideRate = RateUi(
                from = "EUR",
                to = "CAD",
                rate = 1.3
            )

            val state2 = awaitItem()
            assertThat(state2.baseCurrency).isEqualTo("EUR")
            assertThat(state2.manual).hasSize(1)
            assertThat(state2.automatic).hasSize(4)

            assertThat(state2.manual[0].rate).isEqualTo(overrideRate.rate)
            assertThat(state2.automatic[0].rate).isEqualTo(1.0)

            assertThat(state2.manual[0]).isEqualTo(overrideRate)
            assertThat(state2.automatic).doesNotContain(overrideRate)
            assertThat(state2.manual).contains(overrideRate)
        }
    }

}