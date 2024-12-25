package zechs.drive.stream.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zechs.drive.stream.R
import zechs.drive.stream.databinding.FragmentHomeBinding
import zechs.drive.stream.ui.BaseFragment
import zechs.drive.stream.utils.ext.navigateSafe


class HomeFragment : BaseFragment() {

    companion object {
        const val TAG = "HomeFragment"
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(
            inflater, container, /* attachToParent */false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        binding.apply {

            // My drive
            navigateToFiles(
                button = btnMyDrive,
                name = getString(R.string.my_drive),
                query = "'root' in parents and trashed = false"
            )

            // Shared drives
            navigateToFiles(
                button = btnSharedDrives,
                name = getString(R.string.shared_drives),
                query = null
            )

            // Shared with me
            navigateToFiles(
                button = btnSharedWithMe,
                name = getString(R.string.shared_with_me),
                query = "sharedWithMe=true"
            )

            // Starred
            navigateToFiles(
                button = btnStarred,
                name = getString(R.string.starred),
                query = "starred=true"
            )

            // Trashed
            navigateToFiles(
                button = btnTrashed,
                name = getString(R.string.trashed),
                query = "'root' in parents and trashed=true"
            )

            btnAppSettings.setOnClickListener {
                findNavController().navigateSafe(R.id.action_homeFragment_to_settingsFragment)
            }

        }

        setupToolbar()
        observeLogOutState()
        observeAccountName()
    }

    private fun <T : MaterialButton> navigateToFiles(
        button: T, name: String, query: String?
    ) {
        button.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToFilesFragment(
                name = name,
                query = query
            )
            findNavController().navigateSafe(action)
            Log.d(TAG, "navigateToFiles(name=$name, query=$query)")
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_account_switch -> {
                    findNavController().navigateSafe(R.id.action_homeFragment_to_profileFragment)
                    return@setOnMenuItemClickListener true
                }

                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
        }
    }

    private fun observeLogOutState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.hasLoggedOut.collect {
                    if (it) {
                        // restart activity
                        requireActivity().finish()
                        delay(250L)
                        requireActivity().startActivity(requireActivity().intent)
                    }
                }
            }
        }
    }

    private fun observeAccountName() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedAccount.collect { selectedAccount ->
                    binding.tvAccountName.isGone = selectedAccount.isNullOrBlank()
                    binding.tvAccountName.text = selectedAccount
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private var backPressedOnce = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedOnce) {
                    requireActivity().finish()
                } else {
                    backPressedOnce = true
                    Toast.makeText(
                        context,
                        getString(R.string.press_back_again_to_exit),
                        Toast.LENGTH_SHORT
                    ).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        backPressedOnce = false
                    }, 2000)
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}