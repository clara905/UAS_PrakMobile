package com.app.smartkantin.ui.customer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.adapter.MenuCustomerAdapter
import com.app.smartkantin.data.repository.MenuRepository
import com.app.smartkantin.databinding.FragmentMenuCustomerBinding
import com.app.smartkantin.ui.customer.MenuDetailActivity
import com.app.smartkantin.utils.SessionManager
import com.app.smartkantin.viewmodel.CartViewModel
import com.app.smartkantin.viewmodel.CartViewModelFactory
import com.app.smartkantin.viewmodel.MenuViewModel
import com.app.smartkantin.viewmodel.MenuViewModelFactory

class MenuCustomerFragment : Fragment() {

    private var _binding: FragmentMenuCustomerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MenuViewModel
    private lateinit var cartViewModel: CartViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: MenuCustomerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val app = requireActivity().application as SmartKantinApp
        val menuRepository = MenuRepository(app.database.menuDao())
        val cartRepository = com.app.smartkantin.data.repository.CartRepository(app.database.cartDao())

        viewModel = ViewModelProvider(this, MenuViewModelFactory(menuRepository))[MenuViewModel::class.java]
        cartViewModel = ViewModelProvider(this, CartViewModelFactory(cartRepository))[CartViewModel::class.java]

        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MenuCustomerAdapter(
            onItemClick = { menu ->
                val intent = Intent(requireContext(), MenuDetailActivity::class.java)
                intent.putExtra(MenuDetailActivity.EXTRA_MENU_ID, menu.id)
                startActivity(intent)
            },
            onAddClick = { menu ->
                cartViewModel.addToCart(menu, sessionManager.getUserId())
                Toast.makeText(requireContext(), "${menu.namaMenu} ditambahkan", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvMenu.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MenuCustomerFragment.adapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { editable ->
            val query = editable.toString().trim()
            viewModel.searchMenu(query)
        }

        binding.tilSearch.setStartIconOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            viewModel.searchMenu(query)
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearch.text.toString().trim()
                viewModel.searchMenu(query)
                true
            } else false
        }
    }

    private fun observeViewModel() {
        viewModel.allMenu.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvMenu.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvMenu.visibility = View.VISIBLE
                adapter.submitList(list)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}