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
import zechs.drive.stream.data.model.AccountWithClient
import zechs.drive.stream.data.model.TokenRequestBody
import zechs.drive.stream.data.remote.RevokeTokenApi
import zechs.drive.stream.ui.profile.ProfileFragment.Companion.TAG
import zechs.drive.stream.utils.Event
import zechs.drive.stream.utils.SessionManager
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val accountsManager: AccountsDao,
    private val revokeTokenApi: RevokeTokenApi
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

    fun selectAccount(account: AccountWithClient) = viewModelScope.launch(Dispatchers.IO) {
        sessionManager.saveClient(account.getDriveClient())
        sessionManager.saveRefreshToken(account.refreshToken)
        sessionManager.saveAccessToken(account.getAccessTokenResponse())
    }

    fun markDefault(account: AccountWithClient) = viewModelScope.launch(Dispatchers.IO) {
        sessionManager.saveDefault(account.name)
    }

    fun deleteAccount(
        account: AccountWithClient,
        revoke: Boolean
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (account.isDefault) {
            sessionManager.saveDefault(null)
        }
        if (account.refreshToken == sessionManager.fetchRefreshToken()) {
            if (revoke) {
                revokeTokenApi.revokeToken(TokenRequestBody(account.refreshToken))
            }
            Log.d(TAG, "${if (revoke) "" else "Not "}Revoking token")
            val default = sessionManager.fetchDefault()
            sessionManager.resetDataStore()
            if (default != null) {
                accountsManager.getAccount(default)
                    ?.let { account ->
                        selectAccount(account)
                    }
            }
        }
        accountsManager.deleteAccount(account.name)
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

    sealed interface AccountUpdateState {
        object Conflict : AccountUpdateState
        object Updated : AccountUpdateState
    }

    private val _accountUpdate = MutableLiveData<Event<AccountUpdateState>>()
    val accountUpdate: LiveData<Event<AccountUpdateState>>
        get() = _accountUpdate

    fun updateAccountName(
        oldName: String,
        newName: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        val doesExist = accountsManager.getAccount(newName) != null
        Log.d(TAG, "validateAccountName: $doesExist")
        if (doesExist) {
            _accountUpdate.postValue(Event(AccountUpdateState.Conflict))
        } else {
            val isDefault = sessionManager.fetchDefault() == oldName
            if (isDefault) {
                sessionManager.saveDefault(newName)
            }
            accountsManager.updateAccountName(oldName, newName)
            _accountUpdate.postValue(Event(AccountUpdateState.Updated))
        }
    }

}