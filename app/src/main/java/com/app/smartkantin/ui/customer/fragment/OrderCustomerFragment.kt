package com.app.smartkantin.ui.customer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.adapter.OrderAdapter
import com.app.smartkantin.databinding.FragmentOrderCustomerBinding
import com.app.smartkantin.utils.SessionManager
import com.app.smartkantin.viewmodel.OrderViewModel
import com.app.smartkantin.viewmodel.OrderViewModelFactory

class OrderCustomerFragment : Fragment() {

    private var _binding: FragmentOrderCustomerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OrderViewModel
    private lateinit var adapter: OrderAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val app = requireActivity().application as SmartKantinApp
        viewModel = ViewModelProvider(this, OrderViewModelFactory(app.database.orderDao()))[OrderViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = OrderAdapter()
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@OrderCustomerFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.getOrdersByUser(sessionManager.getUserId()).observe(viewLifecycleOwner) { orders ->
            if (orders.isNullOrEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvOrders.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvOrders.visibility = View.VISIBLE
                adapter.submitList(orders)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}