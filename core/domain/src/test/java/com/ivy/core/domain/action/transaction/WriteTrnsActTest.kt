package com.ivy.core.domain.action.transaction

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.algorithm.accountcache.InvalidateAccCacheAct
import com.ivy.core.persistence.algorithm.accountcache.AccountCacheDao
import com.ivy.core.persistence.dao.trn.TransactionDao
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class WriteTrnsActTest {

    private lateinit var transactionDao: TransactionDao
    private lateinit var accountCacheDao: AccountCacheDao
    private lateinit var writeTrnsAct: WriteTrnsAct

    private lateinit var timeProvider: TimeProvider

    @BeforeEach
    fun setUp() {
        timeProvider = object :TimeProvider {
            override fun timeNow(): LocalDateTime {
                return LocalDateTime.now()
            }

            override fun dateNow(): LocalDate {
                return LocalDate.now()
            }

            override fun zoneId(): ZoneId {
                return ZoneId.systemDefault()
            }

        }

        accountCacheDao = AccountCacheDaoFake()
        transactionDao = TransactionDaoFake()
        writeTrnsAct = WriteTrnsAct(
            transactionDao = transactionDao,
            trnsSignal = TrnsSignal(),
            timeProvider = timeProvider,
            accountCacheDao = accountCacheDao,
            invalidateAccCacheAct = InvalidateAccCacheAct(
                accountCacheDao = accountCacheDao,
                timeProvider = timeProvider
            )
        )

    }

    @Test
    fun `Create transaction, is saved`() = runBlocking {
        val transaction = TransactionDataGenerator().generateFakeTransactions(1).first()
        val inputCreateNew = WriteTrnsAct.Input.CreateNew(trn = transaction)

        writeTrnsAct(inputCreateNew)

        val transactions = transactionDao.findAllBlocking()

        assertThat(transactions).isNotEmpty()
        assertThat(transactions.size).isEqualTo(1)
    }
}
