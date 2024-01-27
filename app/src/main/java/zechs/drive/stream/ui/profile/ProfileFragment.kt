package zechs.drive.stream.ui.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import zechs.drive.stream.data.model.AccountWithClient
import zechs.drive.stream.databinding.FragmentProfileBinding
import zechs.drive.stream.ui.BaseFragment
import zechs.drive.stream.ui.add_account.DialogAddAccount
import zechs.drive.stream.ui.profile.adapter.AccountsAdapter


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
    }

    private fun switchAccount(account: AccountWithClient) {
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

        setupRecyclerView()
        setupAccountsObserver()
    }

    private fun showNewAccountDialog() {
        val addDialog = DialogAddAccount(
            requireContext(),
            onNextClickListener = { name ->
                // Go to next step with the name
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