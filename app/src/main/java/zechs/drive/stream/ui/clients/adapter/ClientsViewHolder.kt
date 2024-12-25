package zechs.drive.stream.ui.clients.adapter

import androidx.recyclerview.widget.RecyclerView
import zechs.drive.stream.data.model.Client
import zechs.drive.stream.databinding.ItemClientBinding

class ClientsViewHolder(
    private val itemBinding: ItemClientBinding,
    val clientsAdapter: ClientsAdapter
) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(client: Client) {
        itemBinding.apply {
            textView.text = client.id
            root.setOnClickListener {
                clientsAdapter.onClickListener.invoke(client)
            }
            btnMenu.setOnClickListener {
                clientsAdapter.onMenuClickListener.invoke(btnMenu, client)
            }
        }
    }

}