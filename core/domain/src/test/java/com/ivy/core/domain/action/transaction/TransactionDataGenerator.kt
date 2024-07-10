package com.ivy.core.domain.action.transaction

import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import com.ivy.data.attachment.Attachment
import com.ivy.data.attachment.AttachmentSource
import com.ivy.data.attachment.AttachmentType
import com.ivy.data.category.Category
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType
import com.ivy.data.tag.Tag
import com.ivy.data.tag.TagState
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnMetadata
import com.ivy.data.transaction.TrnState
import com.ivy.data.transaction.dummyTrnTimeActual
import com.ivy.data.transaction.dummyTrnTimeDue
import java.time.LocalDateTime
import java.util.*

fun account(): Account {
    return Account(
        id = UUID.randomUUID(),
        name = "Account",
        currency = "USD",
        color = -16777216, // Black color
        icon = "bank",
        excluded = false,
        folderId = null,
        orderNum = 1.0,
        state = AccountState.Default,
        sync = Sync(
            SyncState.Synced,
            LocalDateTime.now()
        )
    )
}

fun tag(): Tag {
    return Tag(
        id = "tag",
        color = -16711936, // Green color
        name = "Tag ",
        orderNum = 1.0,
        state = TagState.Default,
        sync = Sync(
            SyncState.Synced,
            LocalDateTime.now()
        )
    )
}

fun category(): Category {
    return Category(
        id = UUID.randomUUID(),
        name = "Category",
        type = CategoryType.Expense,
        parentCategoryId = null,
        color = -65536, // Red color
        icon = "shopping_cart",
        orderNum = 1.toDouble(),
        state = CategoryState.Default,
        sync = Sync(
            SyncState.Synced,
            LocalDateTime.now()
        )
    )
}

fun attachment(): Attachment {
    return Attachment(
        id = "attachment",
        associatedId = "transaction",
        uri = "https://example.com/receipt.jpg",
        source = AttachmentSource.Remote,
        filename = "receipt.jpg",
        type = AttachmentType.Image,
        sync = Sync(
            SyncState.Synced,
            LocalDateTime.now()
        )
    )
}

class TransactionDataGenerator {
    fun generateFakeTransactions(count: Int): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        for (i in 1..count) {
            val transaction = Transaction(
                id = UUID.randomUUID(),
                account = Account(
                    id = UUID.randomUUID(),
                    name = "Account $i",
                    currency = "USD",
                    color = -16777216, // Black color
                    icon = "bank",
                    excluded = false,
                    folderId = null,
                    orderNum = i.toDouble(),
                    state = AccountState.Default,
                    sync = Sync(
                        SyncState.Synced,
                        LocalDateTime.now()
                    )
                ),
                type = if (i % 2 == 0) TransactionType.Income else TransactionType.Expense,
                value = Value(
                    amount = (100..1000).random().toDouble(),
                    currency = "USD"
                ),
                category = if (i % 3 == 0) {
                    Category(
                        id = UUID.randomUUID(),
                        name = "Category $i",
                        type = CategoryType.Expense,
                        parentCategoryId = null,
                        color = -65536, // Red color
                        icon = "shopping_cart",
                        orderNum = i.toDouble(),
                        state = CategoryState.Default,
                        sync = Sync(
                            SyncState.Synced,
                            LocalDateTime.now()
                        )
                    )
                } else null,
                time = if (i % 2 == 0) dummyTrnTimeActual() else dummyTrnTimeDue(),
                title = "Transaction $i",
                description = "Description for transaction $i",
                state = TrnState.Default,
                purpose = null,
                tags = listOf(
                    Tag(
                        id = "tag$i",
                        color = -16711936, // Green color
                        name = "Tag $i",
                        orderNum = i.toDouble(),
                        state = TagState.Default,
                        sync = Sync(
                            SyncState.Synced,
                            LocalDateTime.now()
                        )
                    )
                ),
                attachments = listOf(
                    Attachment(
                        id = "attachment$i",
                        associatedId = "transaction$i",
                        uri = "https://example.com/receipt$i.jpg",
                        source = AttachmentSource.Remote,
                        filename = "receipt$i.jpg",
                        type = AttachmentType.Image,
                        sync = Sync(
                            SyncState.Synced,
                            LocalDateTime.now()
                        )
                    )
                ),
                metadata = TrnMetadata(
                    recurringRuleId = null,
                    loanId = null,
                    loanRecordId = null
                ),
                sync = Sync(
                    SyncState.Synced,
                    LocalDateTime.now()
                )
            )
            transactions.add(transaction)
        }
        return transactions
    }
}