package com.app.smartkantin.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.adapter.MenuAdminAdapter
import com.app.smartkantin.data.entity.MenuEntity
import com.app.smartkantin.data.repository.MenuRepository
import com.app.smartkantin.databinding.ActivityKelolaMenuBinding
import com.app.smartkantin.viewmodel.MenuViewModel
import com.app.smartkantin.viewmodel.MenuViewModelFactory

class KelolaMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKelolaMenuBinding
    private lateinit var viewModel: MenuViewModel
    private lateinit var adapter: MenuAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelolaMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as SmartKantinApp
        val repository = MenuRepository(app.database.menuDao())
        viewModel = ViewModelProvider(
            this, MenuViewModelFactory(repository)
        )[MenuViewModel::class.java]

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MenuAdminAdapter(
            onEditClick = { menu ->
                val intent = Intent(this, FormMenuActivity::class.java)
                intent.putExtra(FormMenuActivity.EXTRA_MENU_ID, menu.id)
                startActivity(intent)
            },
            onDeleteClick = { menu -> confirmDelete(menu) }
        )
        binding.rvMenu.layoutManager = LinearLayoutManager(this)
        binding.rvMenu.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.fabAddMenu.setOnClickListener {
            startActivity(Intent(this, FormMenuActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.allMenu.observe(this) { list ->
            adapter.submitList(list)
            binding.tvEmptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun confirmDelete(menu: MenuEntity) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Menu")
            .setMessage("Yakin ingin menghapus \"${menu.namaMenu}\"?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteMenu(menu)
                Toast.makeText(this, "Menu dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}