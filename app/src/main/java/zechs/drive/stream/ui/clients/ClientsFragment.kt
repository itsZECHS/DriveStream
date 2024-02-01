package zechs.drive.stream.ui.clients

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
import zechs.drive.stream.databinding.FragmentClientsBinding
import zechs.drive.stream.ui.BaseFragment
import zechs.drive.stream.ui.clients.adapter.ClientsAdapter


class ClientsFragment : BaseFragment() {

    companion object {
        const val TAG = "ClientsFragment"
    }

    private var _binding: FragmentClientsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ClientsViewModel>()

    private val clientsAdapter by lazy {
        ClientsAdapter(
            onClickListener = { },
            onMenuClickListener = { view, client ->

            })
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}