package zechs.drive.stream.ui.clients

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import zechs.drive.stream.data.local.AccountsDao
import javax.inject.Inject

@HiltViewModel
class ClientsViewModel @Inject constructor(
    private val accountsManager: AccountsDao
) : ViewModel() {

    val clients = accountsManager.getClients()

}