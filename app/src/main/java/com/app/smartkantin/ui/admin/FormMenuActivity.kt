package com.app.smartkantin.ui.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.data.repository.MenuRepository
import com.app.smartkantin.databinding.ActivityFormMenuBinding
import com.app.smartkantin.viewmodel.MenuFormState
import com.app.smartkantin.viewmodel.MenuViewModel
import com.app.smartkantin.viewmodel.MenuViewModelFactory
import com.bumptech.glide.Glide

class FormMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormMenuBinding
    private lateinit var viewModel: MenuViewModel

    private var menuId: Int = 0
    private var selectedImageUri: String = ""
    private val categories = listOf("Makanan", "Minuman", "Cemilan", "Sayuran")

    // ...
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                // Sebagian sumber gambar tidak mendukung persistable permission, abaikan
            }
            selectedImageUri = it.toString()
            Glide.with(this).load(it).centerCrop().into(binding.ivPreview)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as SmartKantinApp
        val repository = MenuRepository(app.database.menuDao())
        viewModel = ViewModelProvider(
            this, MenuViewModelFactory(repository)
        )[MenuViewModel::class.java]

        menuId = intent.getIntExtra(EXTRA_MENU_ID, 0)

        if (menuId != 0) {
            binding.tvFormTitle.text = "Edit Menu"
            loadExistingMenu()
        } else {
            binding.tvFormTitle.text = "Tambah Menu"
        }

        setupCategoryDropdown()
        setupListeners()
        observeViewModel()
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(this, com.app.smartkantin.R.layout.item_dropdown, categories)
        binding.actKategori.setAdapter(adapter)
    }

    private fun loadExistingMenu() {
        viewModel.getMenuById(menuId) { menu ->
            menu?.let {
                binding.etNamaMenu.setText(it.namaMenu)
                binding.etDeskripsi.setText(it.deskripsi)
                binding.actKategori.setText(it.kategori, false)
                binding.etHarga.setText(it.harga.toInt().toString())
                selectedImageUri = it.gambar
                if (it.gambar.isNotBlank()) {
                    Glide.with(this).load(Uri.parse(it.gambar))
                        .centerCrop().into(binding.ivPreview)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            viewModel.saveMenu(
                id = menuId,
                namaMenu = binding.etNamaMenu.text.toString().trim(),
                deskripsi = binding.etDeskripsi.text.toString().trim(),
                hargaText = binding.etHarga.text.toString().trim(),
                gambar = selectedImageUri,
                kategori = binding.actKategori.text.toString().trim()
            )
        }
    }

    private fun observeViewModel() {
        viewModel.formState.observe(this) { state ->
            when (state) {
                is MenuFormState.Loading -> {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.text = "Menyimpan..."
                }
                is MenuFormState.Success -> {
                    Toast.makeText(this, "Menu berhasil disimpan", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is MenuFormState.Error -> {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.text = "Simpan"
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    companion object {
        const val EXTRA_MENU_ID = "extra_menu_id"
    }
}