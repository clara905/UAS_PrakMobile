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
import com.app.smartkantin.data.repository.PromoRepository
import com.app.smartkantin.databinding.ActivityCartBinding
import com.app.smartkantin.utils.Formatter
import com.app.smartkantin.utils.NotificationHelper
import com.app.smartkantin.utils.SessionManager
import com.app.smartkantin.viewmodel.CartViewModel
import com.app.smartkantin.viewmodel.CartViewModelFactory
import com.app.smartkantin.viewmodel.PromoViewModel
import com.app.smartkantin.viewmodel.PromoViewModelFactory

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var viewModel: CartViewModel
    private lateinit var promoViewModel: PromoViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var adapter: CartAdapter
    
    private var currentItems: List<com.app.smartkantin.data.entity.CartItemEntity> = emptyList()
    private var subtotal: Double = 0.0
    private var discount: Double = 0.0
    private var currentTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        notificationHelper = NotificationHelper(this)
        
        val app = application as SmartKantinApp
        val cartRepo = CartRepository(app.database.cartDao(), app.database.orderDao(), app.database.orderItemDao())
        val promoRepo = PromoRepository(app.database.promoDao())
        
        viewModel = ViewModelProvider(this, CartViewModelFactory(cartRepo))[CartViewModel::class.java]
        promoViewModel = ViewModelProvider(this, PromoViewModelFactory(promoRepo))[PromoViewModel::class.java]
        
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
        
        binding.btnApplyPromo.setOnClickListener {
            val code = binding.etPromo.text.toString().trim()
            if (code.isNotEmpty()) {
                promoViewModel.applyPromo(code) { error ->
                    if (error != null) {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Promo berhasil dipasang!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

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
                binding.cardPromo.visibility = View.GONE
                binding.cardCheckout.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvCart.visibility = View.VISIBLE
                binding.cardPromo.visibility = View.VISIBLE
                binding.cardCheckout.visibility = View.VISIBLE
                
                adapter.submitList(items)
                calculateTotal()
            }
        }

        promoViewModel.promoApplied.observe(this) { promo ->
            promo?.let {
                calculateTotal()
            }
        }

        viewModel.checkoutResult.observe(this) { success ->
            if (success) {
                notificationHelper.sendNotification(
                    "Pesanan Berhasil",
                    "Pesanan Anda sudah masuk dan sedang menunggu konfirmasi penjual."
                )
                Toast.makeText(this, "Pesanan berhasil dibuat!", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Gagal membuat pesanan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateTotal() {
        subtotal = currentItems.sumOf { it.harga * it.qty }
        val promo = promoViewModel.promoApplied.value
        discount = if (promo != null) {
            (subtotal * promo.persenPotongan / 100)
        } else {
            0.0
        }
        currentTotal = subtotal - discount
        binding.tvTotalBayar.text = Formatter.rupiah(currentTotal)
    }
}