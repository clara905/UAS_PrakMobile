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
import com.app.smartkantin.databinding.FragmentHomePenjualBinding
import com.app.smartkantin.utils.Formatter
import com.app.smartkantin.utils.OrderStatus
import com.app.smartkantin.utils.SessionManager
import com.app.smartkantin.viewmodel.OrderViewModel
import com.app.smartkantin.viewmodel.OrderViewModelFactory

class HomePenjualFragment : Fragment() {

    private var _binding: FragmentHomePenjualBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePenjualBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val sessionManager = SessionManager(requireContext())
        binding.tvNamaToko.text = sessionManager.getNamaToko() ?: "Kantin Anda"

        val app = requireActivity().application as SmartKantinApp
        orderViewModel = ViewModelProvider(this, OrderViewModelFactory(app.database.orderDao()))[OrderViewModel::class.java]

        setupRecyclerView()
        setupListeners()
        observeData()
    }

    private fun setupListeners() {
        binding.btnManagePromo.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), com.app.smartkantin.ui.admin.KelolaPromoActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(isAdmin = true) { order ->
            orderViewModel.updateStatus(order.id, order.status)
        }
        binding.rvRecentOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }

    private fun observeData() {
        orderViewModel.getAllOrders().observe(viewLifecycleOwner) { orders ->
            if (orders.isNullOrEmpty()) {
                binding.tvEmptyOrders.visibility = View.VISIBLE
                binding.rvRecentOrders.visibility = View.GONE
                binding.tvTotalOmzet.text = "Rp 0"
                binding.tvPesananBaru.text = "0"
            } else {
                binding.tvEmptyOrders.visibility = View.GONE
                binding.rvRecentOrders.visibility = View.VISIBLE
                
                // Tampilkan hanya 3 pesanan terbaru
                orderAdapter.submitList(orders.take(3))

                // Hitung Omzet (Hanya yang sudah SELESAI)
                val totalOmzet = orders.filter { it.status == OrderStatus.SELESAI }
                    .sumOf { it.totalHarga }
                binding.tvTotalOmzet.text = Formatter.rupiah(totalOmzet)

                // Hitung Pesanan Baru (MENUNGGU)
                val pesananBaru = orders.count { it.status == OrderStatus.MENUNGGU }
                binding.tvPesananBaru.text = pesananBaru.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}