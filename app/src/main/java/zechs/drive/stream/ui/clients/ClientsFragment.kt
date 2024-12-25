package zechs.drive.stream.ui.clients

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import zechs.drive.stream.R
import zechs.drive.stream.data.model.Client
import zechs.drive.stream.databinding.FragmentClientsBinding
import zechs.drive.stream.ui.BaseFragment
import zechs.drive.stream.ui.add_client.DialogAddClient
import zechs.drive.stream.ui.clients.adapter.ClientsAdapter
import zechs.drive.stream.utils.ext.navigateSafe


class ClientsFragment : BaseFragment() {

    companion object {
        const val TAG = "ClientsFragment"
    }

    private var _binding: FragmentClientsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ClientsViewModel>()
    private val args by navArgs<ClientsFragmentArgs>()

    private val clientsAdapter by lazy {
        ClientsAdapter(
            onClickListener = { client ->
                navigateToLogin(client)
            },
            onMenuClickListener = { view, client ->
                showClientMenu(view, client)
            })
    }

    private fun showClientMenu(view: View, client: Client) {
        PopupMenu(requireContext(), view).apply {
            inflate(R.menu.client_menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem?.itemId) {
                    R.id.client_action_edit -> {
                        showEditDialog(client)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.client_action_delete -> {
                        handleClientDelete(client)
                        return@setOnMenuItemClickListener true
                    }
                }
                return@setOnMenuItemClickListener false
            }
        }.also { it.show() }
    }

    private fun showEditDialog(client: Client) {
        showClientDialog(client, onSubmitClickListener = { viewModel.updateClient(it) })
    }

    private fun showAddDialog() {
        showClientDialog(null, onSubmitClickListener = { viewModel.addClient(it) })
    }

    private fun showClientDialog(client: Client?, onSubmitClickListener: (Client) -> Unit) {
        val editDialog = DialogAddClient(
            requireContext(),
            client = client,
            onSubmitClickListener = onSubmitClickListener
        )
        editDialog.also {
            it.show()
            it.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    private fun handleClientDelete(client: Client) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.confirm_delete_client_title))
            .setMessage(getString(R.string.confirm_delete_client_warning))
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                viewModel.deleteClient(client)
                dialog.dismiss()
                Log.d(TAG, "Deleting client ${client.id} along with all associated accounts.")
            }
            .show()
    }


    private fun navigateToLogin(client: Client) {
        val nickname = args.nickname
        val action = ClientsFragmentDirections.actionClientsFragmentToLoginFragment(
            nickname,
            client.id, client.secret, client.redirectUri
        )
        findNavController().navigateSafe(action)
        Log.d(TAG, "navigateToLogin(nickname=$nickname, clientId=${client.id})")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientsBinding.inflate(
            inflater, container, /* attachToParent */false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentClientsBinding.bind(view)

        setupRecyclerView()
        setupClientsObserver()
        setupAddClientFab()
    }

    private fun setupAddClientFab() {
        binding.btnAddClient.setOnClickListener { showAddDialog() }
    }

    private fun setupClientsObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.clients.collect { clients ->
                    clientsAdapter.submitList(clients)
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
            adapter = clientsAdapter
            layoutManager = linearLayoutManager
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}