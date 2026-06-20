package com.app.smartkantin.ui.customer

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.adapter.CartAdapter
import com.app.smartkantin.data.repository.CartRepository
import com.app.smartkantin.databinding.ActivityCartBinding
import com.app.smartkantin.utils.Formatter
import com.app.smartkantin.utils.SessionManager
import com.app.smartkantin.viewmodel.CartViewModel
import com.app.smartkantin.viewmodel.CartViewModelFactory

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var viewModel: CartViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: CartAdapter
    private var currentItems: List<com.app.smartkantin.data.entity.CartItemEntity> = emptyList()
    private var currentTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        val app = application as SmartKantinApp
        val repository = CartRepository(
            app.database.cartDao(),
            app.database.orderDao(),
            app.database.orderItemDao()
        )
        viewModel = ViewModelProvider(this, CartViewModelFactory(repository))[CartViewModel::class.java]
        viewModel.initUserId(sessionManager.getUserId())

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter { item, delta ->
            viewModel.updateQty(item, delta)
        }
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = this@CartActivity.adapter
        }
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        
        binding.btnCheckout.setOnClickListener {
            if (currentItems.isNotEmpty()) {
                viewModel.checkout(sessionManager.getUserId(), currentItems, currentTotal)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.cartItems.observe(this) { items ->
            currentItems = items
            if (items.isNullOrEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvCart.visibility = View.GONE
                binding.cardCheckout.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvCart.visibility = View.VISIBLE
                binding.cardCheckout.visibility = View.VISIBLE
                
                adapter.submitList(items)
                
                // Menghitung total harga secara otomatis setiap ada perubahan (Qty + atau -)
                currentTotal = items.sumOf { it.harga * it.qty }
                binding.tvTotalBayar.text = Formatter.rupiah(currentTotal)
            }
        }

        viewModel.checkoutResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Pesanan berhasil dibuat!", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Gagal membuat pesanan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}