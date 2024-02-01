package zechs.drive.stream.ui.clients.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import zechs.drive.stream.R
import zechs.drive.stream.data.model.Client
import zechs.drive.stream.databinding.ItemClientBinding

class ClientsAdapter(
    val onClickListener: (Client) -> Unit,
    val onMenuClickListener: (View, Client) -> Unit
) : ListAdapter<Client, ClientsViewHolder>(ClientsItemDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) = ClientsViewHolder(
        itemBinding = ItemClientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        ),
        clientsAdapter = this
    )

    override fun onBindViewHolder(holder: ClientsViewHolder, position: Int) {
        val item = getItem(position)
        return holder.bind(item)
    }

    override fun getItemViewType(
        position: Int
    ) = R.layout.item_client

}