package com.ivy.core.domain.action.account

import assertk.Assert
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.ivy.account
import com.ivy.common.androidtest.IvyAndroidTest
import com.ivy.common.time.toLocal
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.persistence.dao.account.AccountDao
import com.ivy.core.persistence.entity.account.AccountEntity
import com.ivy.data.Sync
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import com.ivy.data.SyncState
import com.ivy.data.account.Account
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class WriteAccountsActTest: IvyAndroidTest() {

    @Inject
    lateinit var writeAccountsAct: WriteAccountsAct

    @Inject
    lateinit var accountDao: AccountDao

    @Test
    fun testSaveUpdateAccount() = runTest {
        val syncTime = LocalDateTime
            .now()
            .plusHours(3)
            .truncatedTo(ChronoUnit.SECONDS)

        val accountToSave = account().copy(
            sync = Sync(SyncState.Syncing, syncTime)
        )

        writeAccountsAct(Modify.save(accountToSave))

        val createdAccountFromDb = accountDao.findAllBlocking().first()

        assertThat(createdAccountFromDb)
            .transformToAccount()
            .isEqualTo(accountToSave)

        val updatedAccount = accountToSave.copy(
            name = "updated name",
            currency = "CAD"
        )
        writeAccountsAct(Modify.save(updatedAccount))

        val accountFromDb = accountDao.findAllBlocking()

        assertThat(accountFromDb).hasSize(1)

        val updatedAccountFromDb = accountFromDb.first()

        assertThat(updatedAccountFromDb)
            .transformToAccount()
            .isEqualTo(updatedAccount)

    }

    private fun Assert<AccountEntity>.transformToAccount(): Assert<Account> {
        return transform { entity ->
            Account(
                id = UUID.fromString(entity.id),
                name = entity.name,
                color = entity.color,
                currency = entity.currency,
                icon = entity.icon,
                excluded = entity.excluded,
                folderId = entity.folderId?.let { UUID.fromString(it) },
                orderNum = entity.orderNum,
                state = entity.state,
                sync = Sync(
                    entity.sync,
                    LocalDateTime
                        .ofInstant(entity.lastUpdated, ZoneId.systemDefault()))
                )
        }

    }

}