package zechs.drive.stream.ui.login

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zechs.drive.stream.data.local.AccountsDao
import zechs.drive.stream.data.model.Account
import zechs.drive.stream.data.model.AuthorizationResponse
import zechs.drive.stream.data.model.DriveClient
import zechs.drive.stream.data.repository.DriveRepository
import zechs.drive.stream.utils.Event
import zechs.drive.stream.utils.state.Resource
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountsManager: AccountsDao,
    private val driveRepository: DriveRepository,
    private val gson: Gson
) : ViewModel() {

    private val _loginStatus = MutableLiveData<Event<Resource<AuthorizationResponse>>>()
    val loginStatus: LiveData<Event<Resource<AuthorizationResponse>>>
        get() = _loginStatus

    fun addAccount(
        name: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        authCodeUri: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        val client = DriveClient(
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = redirectUri,
            scopes = listOf("https://www.googleapis.com/auth/drive")
        )
        _loginStatus.postValue(Event(Resource.Loading()))
        val authCode = Uri.parse(authCodeUri).getQueryParameter("code")
        if (authCode == null) {
            _loginStatus.postValue(Event(Resource.Error("Authorization code not found, please check url")))
        } else {
            val response = driveRepository.fetchRefreshToken(client, authCode)
            if (response is Resource.Success) {
                val data = response.data!!
                accountsManager.addAccount(
                    Account(name, data.refreshToken, gson.toJson(data.toTokenResponse()), clientId)
                )
            }
            _loginStatus.postValue(Event(response))
        }
    }
}

