package com.app.smartkantin.ui.admin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.adapter.OrderAdapter
import com.app.smartkantin.databinding.FragmentOrderPenjualBinding
import com.app.smartkantin.utils.NotificationHelper
import com.app.smartkantin.utils.OrderStatus
import com.app.smartkantin.viewmodel.OrderViewModel
import com.app.smartkantin.viewmodel.OrderViewModelFactory

class OrderPenjualFragment : Fragment() {

    private var _binding: FragmentOrderPenjualBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OrderViewModel
    private lateinit var adapter: OrderAdapter
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderPenjualBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationHelper = NotificationHelper(requireContext())
        val app = requireActivity().application as SmartKantinApp
        viewModel = ViewModelProvider(this, OrderViewModelFactory(app.database.orderDao()))[OrderViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = OrderAdapter(isAdmin = true) { order ->
            val nextStatus = when (order.status) {
                com.app.smartkantin.utils.OrderStatus.MENUNGGU -> "Sedang Diproses"
                com.app.smartkantin.utils.OrderStatus.DIPROSES -> "Siap Diambil"
                else -> ""
            }
            if (nextStatus.isNotEmpty()) {
                notificationHelper.sendNotification(
                    "Update Pesanan",
                    "Pesanan #${order.id} $nextStatus!"
                )
            }
            viewModel.updateStatus(order.id, order.status)
        }
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@OrderPenjualFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.getAllOrders().observe(viewLifecycleOwner) { orders ->
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