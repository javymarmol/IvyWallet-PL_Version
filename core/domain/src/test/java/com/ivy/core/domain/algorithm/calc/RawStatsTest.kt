package com.ivy.core.domain.algorithm.calc

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.transaction.TransactionType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

/**
 * Created by Heyner Javier Marmol @javymarmol on 29/05/24.
 * javymarmol.com
 * Copyright (c) 2024 JavyMarmol. All rights reserved.
 */
class RawStatsTest {
    private lateinit var rawStats: RawStats
    private lateinit var trans: List<CalcTrn>

    @BeforeEach
    fun setUp() {
        val time =  Instant.now()
        trans = listOf(
            CalcTrn(
                amount = 12.5,
                currency = "USD",
                type = TransactionType.Income,
                time = time
            ),
            CalcTrn(
                amount = 125000.0,
                currency = "COP",
                type = TransactionType.Income,
                time = time
            ),
            CalcTrn(
                amount = 35.5,
                currency = "EUR",
                type = TransactionType.Income,
                time = time
            ),
            CalcTrn(
                amount = 12.5,
                currency = "USD",
                type = TransactionType.Expense,
                time = time
            ),
        )
        rawStats = RawStats(
            incomes = mapOf(
                "USD" to 12.5,
                "COP" to 125000.0,
                "EUR" to 35.5,
            ),
            expenses = mapOf(
                "USD" to 12.5,
            ),
            incomesCount = 3,
            expensesCount = 1,
            newestTrnTime = time,
        )
    }

    @Test
    fun `given a list of calc trn return aCorrect raw stats`() {
        val result = rawStats(trans)
        assertThat(result).isEqualTo(rawStats)
    }

    @Test
    fun `Test creating raw stats from transactions`() {
        val tenSecondsAgo = Instant.now().minusSeconds(10)
        val fiveSecondsAgo = Instant.now().minusSeconds(5)
        val threeSecondsAgo = Instant.now().minusSeconds(5)
        val stats = rawStats(
            listOf(
                CalcTrn(
                    amount = 5.0,
                    currency = "EUR",
                    type = TransactionType.Income,
                    time = tenSecondsAgo
                ),
                CalcTrn(
                    amount = 3.0,
                    currency = "USD",
                    type = TransactionType.Expense,
                    time = threeSecondsAgo
                ),
                CalcTrn(
                    amount = 10.0,
                    currency = "USD",
                    type = TransactionType.Expense,
                    time = fiveSecondsAgo
                ),
            )
        )

        assertThat(stats.expensesCount).isEqualTo(2)
        assertThat(stats.newestTrnTime).isEqualTo(threeSecondsAgo)
        assertThat(stats.expenses).isEqualTo(mapOf("USD" to 13.0))

        assertThat(stats.incomesCount).isEqualTo(1)
        assertThat(stats.incomes).isEqualTo(mapOf("EUR" to 5.0))
    }

}