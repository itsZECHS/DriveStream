package zechs.drive.stream.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import zechs.drive.stream.data.local.AccountsDao
import zechs.drive.stream.utils.SessionManager
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    accountsManager: AccountsDao
) : ViewModel() {

    val accounts = accountsManager
        .getAccounts()
        .map { accounts ->
            val default = sessionManager.fetchDefault()
            accounts.map { account ->
                account.isDefault = account.name == default
                account
            }
        }

}