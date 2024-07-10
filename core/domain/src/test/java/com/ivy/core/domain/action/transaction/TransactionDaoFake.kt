package com.ivy.core.domain.action.transaction

import androidx.sqlite.db.SupportSQLiteQuery
import com.ivy.core.persistence.dao.trn.AccountIdAndTrnTime
import com.ivy.core.persistence.dao.trn.SaveTrnData
import com.ivy.core.persistence.dao.trn.TransactionDao
import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.core.persistence.entity.trn.TransactionEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.TrnTagEntity
import com.ivy.data.SyncState
import kotlinx.coroutines.flow.MutableStateFlow

class TransactionDaoFake : TransactionDao() {
    private val transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    private val attachments = MutableStateFlow<List<AttachmentEntity>>(emptyList())
    private val metadata = MutableStateFlow<List<TrnMetadataEntity>>(emptyList())
    private val tags = MutableStateFlow<List<TrnTagEntity>>(emptyList())

    override suspend fun save(data: SaveTrnData) {

        val trnId = data.entity.id
        saveTrnEntity(data.entity)


        // Delete existing tags
        updateTrnTagsSyncByTrnId(trnId, sync = SyncState.Deleting)
        saveTags(data.tags)


        // Delete existing attachments

        updateAttachmentsSyncByAssociatedId(trnId, sync = SyncState.Deleting)
        saveAttachments(data.attachments)

        // Delete existing metadata key-values
        updateMetadataSyncByTrnId(trnId, sync = SyncState.Deleting)
        saveMetadata(data.metadata)
    }

    override suspend fun saveTrnEntity(entity: TransactionEntity) {

        val transaction = transactions.value.firstOrNull { it.id == entity.id }

        transactions.value = if (transaction == null) {
            transactions.value + entity
        } else {
            transactions.value.map {
                if (it.id == entity.id)
                    return@map it.copy(sync = SyncState.Deleting)
                return@map it
            }
        }
    }

    override suspend fun updateTrnTagsSyncByTrnId(trnId: String, sync: SyncState) {
        tags.value = tags.value
            .map { tag ->
                if (tag.trnId == trnId) {
                    return@map tag.copy(sync = sync)
                }
                return@map tag

            }
    }

    override suspend fun saveTags(entity: List<TrnTagEntity>) {

        entity.forEach { t ->
            val tag = tags.value.firstOrNull { it.tagId == t.tagId && it.trnId == t.trnId }

            tags.value = if (tag == null) {
                tags.value + entity
            } else {
                tags.value.map {
                    if (it.tagId == t.tagId && it.trnId == t.trnId)
                        return@map it.copy(
                            lastUpdated = t.lastUpdated,
                            sync = t.sync
                        )
                    return@map it
                }
            }
        }
    }

    override suspend fun updateAttachmentsSyncByAssociatedId(
        associatedId: String,
        sync: SyncState
    ) {
        attachments.value = attachments.value
            .map {
                if (it.associatedId == associatedId)
                    return@map it.copy(sync = sync)
                return@map it
            }
    }

    override suspend fun saveAttachments(entity: List<AttachmentEntity>) {

        entity.forEach { t ->
            val at = attachments.value.firstOrNull { it.id == t.id }

            attachments.value = if (at == null) {
                attachments.value + entity
            } else {
                attachments.value.map {
                    if (it.id == t.id)
                        return@map it.copy(
                            uri = t.uri,
                            source = t.source,
                            filename = t.filename,
                            type = t.type,
                            lastUpdated = t.lastUpdated,
                            sync = t.sync
                        )
                    return@map it
                }
            }
        }
    }

    override suspend fun updateMetadataSyncByTrnId(trnId: String, sync: SyncState) {

        metadata.value = metadata.value
        .map {
            if (it.trnId == trnId)
                return@map it.copy(sync = SyncState.Deleting)
            return@map it
        }
    }

    override suspend fun saveMetadata(entity: List<TrnMetadataEntity>) {
        entity.forEach { m ->
            val meta = metadata.value.firstOrNull{ it.id == m.id }
            metadata.value = if (meta == null){
                metadata.value + m
            } else {
                metadata.value
                    .map {
                        if (it.id == m.id)
                            return@map m
                        return@map it
                    }
            }
        }
    }

    override suspend fun findAllBlocking(): List<TransactionEntity> {
        return transactions.value
    }

    override suspend fun findBySQL(query: SupportSQLiteQuery): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAccountIdAndTimeById(trnId: String): AccountIdAndTrnTime? {
        TODO("Not yet implemented")
    }

    override suspend fun updateTrnEntitySyncById(trnId: String, sync: SyncState) {
        TODO("Not yet implemented")
    }
}