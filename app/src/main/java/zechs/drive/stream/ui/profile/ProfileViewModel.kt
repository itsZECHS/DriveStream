package zechs.drive.stream.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import zechs.drive.stream.data.local.AccountsDao
import zechs.drive.stream.ui.profile.ProfileFragment.Companion.TAG
import zechs.drive.stream.utils.Event
import zechs.drive.stream.utils.SessionManager
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val accountsManager: AccountsDao
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

    sealed interface AccountValidationState {
        object Conflict : AccountValidationState
        data class Valid(val name: String) : AccountValidationState
    }

    private val _accountName = MutableLiveData<Event<AccountValidationState>>()
    val accountName: LiveData<Event<AccountValidationState>>
        get() = _accountName

    fun validateAccountName(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val doesExist = accountsManager.getAccount(name) != null
        Log.d(TAG, "validateAccountName: $doesExist")
        if (doesExist) {
            _accountName.postValue(Event(AccountValidationState.Conflict))
        } else {
            _accountName.postValue(Event(AccountValidationState.Valid(name)))
        }
    }

}