package zechs.drive.stream.ui.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zechs.drive.stream.data.local.AccountsDao
import zechs.drive.stream.data.model.Client
import javax.inject.Inject

@HiltViewModel
class ClientsViewModel @Inject constructor(
    private val accountsManager: AccountsDao
) : ViewModel() {

    val clients = accountsManager.getClients()

    fun addClient(client : Client) = viewModelScope.launch(Dispatchers.IO) {
        accountsManager.addClient(client)
    }
    fun updateClient(updated: Client) = viewModelScope.launch(Dispatchers.IO) {
        accountsManager.updateClient(updated.id, updated.secret, updated.redirectUri)
    }

    fun deleteClient(client: Client) = viewModelScope.launch(Dispatchers.IO) {
        accountsManager.deleteClient(client.id)
    }

}