package com.app.smartkantin.ui.customer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.data.repository.MenuRepository
import com.app.smartkantin.databinding.ActivityMenuDetailBinding
import com.app.smartkantin.utils.Formatter
import com.app.smartkantin.utils.SessionManager
import com.app.smartkantin.viewmodel.CartViewModel
import com.app.smartkantin.viewmodel.CartViewModelFactory
import com.app.smartkantin.viewmodel.MenuViewModel
import com.app.smartkantin.viewmodel.MenuViewModelFactory
import com.bumptech.glide.Glide

class MenuDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuDetailBinding
    private lateinit var viewModel: MenuViewModel
    private lateinit var cartViewModel: CartViewModel
    private lateinit var sessionManager: SessionManager
    private var menuId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        menuId = intent.getIntExtra(EXTRA_MENU_ID, -1)
        if (menuId == -1) {
            finish()
            return
        }

        val app = application as SmartKantinApp
        val menuRepository = MenuRepository(app.database.menuDao())
        val cartRepository = com.app.smartkantin.data.repository.CartRepository(app.database.cartDao())
        
        viewModel = ViewModelProvider(this, MenuViewModelFactory(menuRepository))[MenuViewModel::class.java]
        cartViewModel = ViewModelProvider(this, CartViewModelFactory(cartRepository))[CartViewModel::class.java]

        setupToolbar()
        observeViewModel()
        
        binding.btnAddToCart.setOnClickListener {
            viewModel.getMenuById(menuId) { menu ->
                menu?.let {
                    cartViewModel.addToCart(it, sessionManager.getUserId())
                    Toast.makeText(this, "Berhasil ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun observeViewModel() {
        viewModel.getMenuById(menuId) { menu ->
            menu?.let {
                binding.tvNamaMenu.text = it.namaMenu
                binding.tvHarga.text = Formatter.rupiah(it.harga)
                binding.tvTotalHarga.text = Formatter.rupiah(it.harga)
                binding.tvDeskripsi.text = it.deskripsi

                Glide.with(this)
                    .load(it.gambar.ifBlank { null })
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(binding.ivMenu)
            }
        }
    }

    companion object {
        const val EXTRA_MENU_ID = "extra_menu_id"
    }
}