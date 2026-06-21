package com.app.smartkantin.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.adapter.PromoAdapter
import com.app.smartkantin.data.repository.PromoRepository
import com.app.smartkantin.databinding.ActivityKelolaPromoBinding
import com.app.smartkantin.databinding.DialogAddPromoBinding
import com.app.smartkantin.viewmodel.PromoViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.app.smartkantin.viewmodel.PromoViewModelFactory

class KelolaPromoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKelolaPromoBinding
    private lateinit var viewModel: PromoViewModel
    private lateinit var adapter: PromoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelolaPromoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as SmartKantinApp
        val repository = PromoRepository(app.database.promoDao())
        viewModel = ViewModelProvider(this, PromoViewModelFactory(repository))[PromoViewModel::class.java]

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = PromoAdapter { promo ->
            viewModel.deletePromo(promo)
        }
        binding.rvPromo.apply {
            layoutManager = LinearLayoutManager(this@KelolaPromoActivity)
            adapter = this@KelolaPromoActivity.adapter
        }
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.fabAddPromo.setOnClickListener { showAddPromoDialog() }
    }

    private fun observeViewModel() {
        viewModel.allPromos.observe(this) { list ->
            adapter.submitList(list)
        }
    }

    private fun showAddPromoDialog() {
        val dialogBinding = DialogAddPromoBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(dialogBinding.root)

        builder.setPositiveButton("Simpan") { _, _ ->
            val kode = dialogBinding.etKode.text.toString().trim()
            val desc = dialogBinding.etDesc.text.toString().trim()
            val persen = dialogBinding.etPersen.text.toString().toIntOrNull() ?: 0

            if (kode.isNotEmpty() && desc.isNotEmpty() && persen > 0) {
                viewModel.addPromo(kode, desc, persen)
            } else {
                Toast.makeText(this, "Data tidak valid", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Batal", null)
        builder.show()
    }
}