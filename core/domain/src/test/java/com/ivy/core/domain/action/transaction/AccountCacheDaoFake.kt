package com.ivy.core.domain.action.transaction

import com.ivy.core.persistence.algorithm.accountcache.AccountCacheDao
import com.ivy.core.persistence.algorithm.accountcache.AccountCacheEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant

class AccountCacheDaoFake: AccountCacheDao {

    private val accountCache = MutableStateFlow<List<AccountCacheEntity?>>(emptyList())

    override fun findAccountCache(accountId: String): Flow<AccountCacheEntity?> {
        TODO("Not yet implemented")
    }

    override suspend fun findTimestampById(accountId: String): Instant? {
        return accountCache.value.firstOrNull { it?.accountId == accountId }?.timestamp
    }

    override suspend fun save(cache: AccountCacheEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(accountId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }
}