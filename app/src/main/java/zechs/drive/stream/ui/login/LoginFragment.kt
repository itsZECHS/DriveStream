package zechs.drive.stream.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import zechs.drive.stream.R
import zechs.drive.stream.data.model.DriveClient
import zechs.drive.stream.databinding.FragmentLoginBinding
import zechs.drive.stream.ui.BaseFragment
import zechs.drive.stream.utils.state.Resource

class LoginFragment : BaseFragment() {

    companion object {
        const val TAG = "LoginFragment"
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<LoginViewModel>()
    private val args by navArgs<LoginFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(
            inflater, container, /* attachToParent */false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding.tvClientId.text = getString(R.string.client_id_used, args.clientId)

        binding.btnLaunch.setOnClickListener {
            val driveClient = DriveClient(
                clientId = args.clientId,
                clientSecret = args.clientSecret,
                redirectUri = args.redirectUri,
                scopes = listOf("https://www.googleapis.com/auth/drive")
            )
            Log.d(TAG, "Auth url: ${driveClient.authUrl()}")

            Intent().setAction(Intent.ACTION_VIEW)
                .setData(driveClient.authUrl())
                .also { startActivity(it) }
        }

        binding.btnLogin.setOnClickListener {
            val authUrl = binding.tfClientSecret.editText!!.text.toString()
            if (authUrl.isEmpty()) {
                showSnackbar(getString(R.string.empty_auth_url_message))
            } else {
                viewModel.addAccount(
                    args.nickname,
                    args.clientId, args.clientSecret,
                    args.redirectUri, authUrl
                )
            }
        }

        setupLoginObserver()
    }

    private fun setupLoginObserver() {
        viewModel.loginStatus.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Error -> {
                        showSnackbar(response.message)
                        isLoading(false)
                    }

                    is Resource.Loading -> {
                        isLoading(true)
                    }

                    is Resource.Success -> {
                        findNavController().navigate(R.id.action_loginFragment_to_profileFragment)
                    }
                }
            }
        }
    }

    private fun isLoading(loading: Boolean) {
        binding.progressBar.isGone = !loading
        binding.actions.isGone = loading
    }

    private fun showSnackbar(message: String?) {
        Snackbar.make(
            binding.root,
            message ?: getString(R.string.something_went_wrong),
            Snackbar.LENGTH_LONG
        ).show()
    }

}