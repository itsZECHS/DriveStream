package zechs.drive.stream.ui.clients.adapter

import androidx.recyclerview.widget.DiffUtil
import zechs.drive.stream.data.model.Client

class ClientsItemDiffCallback : DiffUtil.ItemCallback<Client>() {

    override fun areItemsTheSame(
        oldItem: Client,
        newItem: Client
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Client, newItem: Client
    ) = oldItem == newItem

}