package com.app.smartkantin.ui.admin.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.adapter.MenuAdminAdapter
import com.app.smartkantin.data.repository.MenuRepository
import com.app.smartkantin.databinding.FragmentMenuPenjualBinding
import com.app.smartkantin.ui.admin.FormMenuActivity
import com.app.smartkantin.viewmodel.MenuViewModel
import com.app.smartkantin.viewmodel.MenuViewModelFactory

class MenuPenjualFragment : Fragment() {

    private var _binding: FragmentMenuPenjualBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MenuViewModel
    private lateinit var adapter: MenuAdminAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuPenjualBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as SmartKantinApp
        val repository = MenuRepository(app.database.menuDao())
        viewModel = ViewModelProvider(this, MenuViewModelFactory(repository))[MenuViewModel::class.java]

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MenuAdminAdapter(
            onEditClick = { menu ->
                val intent = Intent(requireContext(), FormMenuActivity::class.java)
                intent.putExtra(FormMenuActivity.EXTRA_MENU_ID, menu.id)
                startActivity(intent)
            },
            onDeleteClick = { menu ->
                viewModel.deleteMenu(menu)
            }
        )
        binding.rvMenu.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMenu.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAddMenu.setOnClickListener {
            startActivity(Intent(requireContext(), FormMenuActivity::class.java))
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