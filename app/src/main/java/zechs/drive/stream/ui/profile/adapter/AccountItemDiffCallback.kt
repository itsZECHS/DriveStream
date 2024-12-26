package zechs.drive.stream.ui.profile.adapter

import androidx.recyclerview.widget.DiffUtil
import zechs.drive.stream.data.model.AccountWithClient

class AccountItemDiffCallback : DiffUtil.ItemCallback<AccountWithClient>() {

    override fun areItemsTheSame(
        oldItem: AccountWithClient,
        newItem: AccountWithClient
    ): Boolean = oldItem.name == newItem.name

    override fun areContentsTheSame(
        oldItem: AccountWithClient, newItem: AccountWithClient
    ) = oldItem == newItem

}