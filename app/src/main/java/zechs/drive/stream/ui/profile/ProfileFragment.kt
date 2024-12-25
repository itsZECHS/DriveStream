package zechs.drive.stream.ui.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import zechs.drive.stream.R
import zechs.drive.stream.data.model.AccountWithClient
import zechs.drive.stream.databinding.FragmentProfileBinding
import zechs.drive.stream.ui.BaseFragment
import zechs.drive.stream.ui.add_account.DialogAddAccount
import zechs.drive.stream.ui.edit_account.DialogEditAccount
import zechs.drive.stream.ui.profile.ProfileViewModel.AccountUpdateState
import zechs.drive.stream.ui.profile.ProfileViewModel.AccountValidationState
import zechs.drive.stream.ui.profile.adapter.AccountsAdapter
import zechs.drive.stream.utils.ext.navigateSafe


class ProfileFragment : BaseFragment() {

    companion object {
        const val TAG = "ProfileFragment"
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ProfileViewModel>()

    private val accountsAdapter by lazy {
        AccountsAdapter(
            onClickListener = { switchAccount(it) },
            onMenuClickListener = { view, account ->
                showAccountMenu(view, account)
            }
        )
    }

    private fun showAccountMenu(view: View, account: AccountWithClient) {
        PopupMenu(requireContext(), view).apply {
            inflate(R.menu.profile_menu)
            menu.findItem(R.id.action_set_as_default).isVisible = !account.isDefault
            setOnMenuItemClickListener { menuItem ->
                when (menuItem?.itemId) {
                    R.id.action_set_as_default -> {
                        handleSetDefault(account)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.action_rename -> {
                        showEditDialog(account)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.action_delete -> {
                        handleDeleteAccount(account)
                        return@setOnMenuItemClickListener true
                    }
                }
                return@setOnMenuItemClickListener false
            }
        }.also { it.show() }
    }

    private fun showEditDialog(account: AccountWithClient) {
        val editDialog = DialogEditAccount(
            requireContext(),
            name = account.name,
            onUpdateClickListener = { newName ->
                viewModel.updateAccountName(account.name, newName)
            }
        )
        editDialog.also {
            it.show()
            it.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    private fun handleDeleteAccount(account: AccountWithClient) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.confirm_delete_account_title))
            .setMessage(getString(R.string.confirm_delete_account_warning))
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton(getString(R.string.just_delete)) { dialog, _ ->
                viewModel.deleteAccount(account, revoke = false)
                dialog.dismiss()
                Log.d(TAG, "Deleting account $account")
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                viewModel.deleteAccount(account, revoke = true)
                dialog.dismiss()
                Log.d(TAG, "Deleting account $account")
            }
            .show()
    }

    private fun handleSetDefault(account: AccountWithClient) {
        viewModel.markDefault(account)
        findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
    }

    private fun switchAccount(account: AccountWithClient) {
        viewModel.selectAccount(account)
        findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(
            inflater, container, /* attachToParent */false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        binding.btnAddAccount.setOnClickListener {
            showNewAccountDialog()
        }

        newAccountObserver()
        accountUpdateObserver()
        setupRecyclerView()
        setupAccountsObserver()
    }

    private fun showNewAccountDialog() {
        val addDialog = DialogAddAccount(
            requireContext(),
            onNextClickListener = { name ->
                viewModel.validateAccountName(name)
            }
        )
        addDialog.also {
            it.show()
            it.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    private fun setupAccountsObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.accounts.collect { accounts ->
                    accountsAdapter.submitList(accounts)
                }
            }
        }
    }

    private fun newAccountObserver() {
        viewModel.accountName.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { validation ->
                Log.d(TAG, "newAccountObserver: $validation")
                when (validation) {
                    AccountValidationState.Conflict -> {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.account_already_exists),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    is AccountValidationState.Valid -> {
                        // Navigate to next step with the nickname
                        navigateToClients(validation.name)
                    }
                }
            }
        }
    }

    private fun navigateToClients(nickname: String) {
        val action = ProfileFragmentDirections.actionProfileFragmentToClientsFragment(nickname)
        findNavController().navigateSafe(action)
        Log.d(TAG, "navigateToClients(nickname=$nickname)")
    }

    private fun accountUpdateObserver() {
        viewModel.accountUpdate.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { validation ->
                Log.d(TAG, "accountUpdateObserver: $validation")
                when (validation) {
                    AccountUpdateState.Conflict -> {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.account_already_exists),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    is AccountUpdateState.Updated -> {
                        Log.d(TAG, "accountUpdateObserver: updated")
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(
            /* context */ context,
            /* orientation */ RecyclerView.VERTICAL,
            /* reverseLayout */ false
        )
        binding.rvList.apply {
            adapter = accountsAdapter
            layoutManager = linearLayoutManager
        }
    }

}