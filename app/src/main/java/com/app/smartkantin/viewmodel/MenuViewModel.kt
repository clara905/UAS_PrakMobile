package com.app.smartkantin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.app.smartkantin.data.entity.MenuEntity
import com.app.smartkantin.data.repository.MenuRepository
import kotlinx.coroutines.launch

sealed class MenuFormState {
    object Idle : MenuFormState()
    object Loading : MenuFormState()
    object Success : MenuFormState()
    data class Error(val message: String) : MenuFormState()
}

class MenuViewModel(private val repository: MenuRepository) : ViewModel() {

    private val _searchQuery = MutableLiveData<String>("")
    private val _categoryFilter = MutableLiveData<String>("")
    
    // Combine search and category
    private val _menuTrigger = androidx.lifecycle.MediatorLiveData<Pair<String, String>>().apply {
        addSource(_searchQuery) { query -> value = Pair(query ?: "", _categoryFilter.value ?: "") }
        addSource(_categoryFilter) { category -> value = Pair(_searchQuery.value ?: "", category ?: "") }
    }

    val allMenu: LiveData<List<MenuEntity>> = _menuTrigger.switchMap { (query, category) ->
        when {
            !category.isNullOrBlank() -> repository.getMenuByCategory(category).asLiveData()
            !query.isNullOrBlank() -> repository.searchMenu(query).asLiveData()
            else -> repository.getAllMenu().asLiveData()
        }
    }

    fun searchMenu(query: String) {
        _categoryFilter.value = "" // Reset category when searching
        _searchQuery.value = query
    }

    fun filterByCategory(category: String) {
        _searchQuery.value = "" // Reset search when filtering by category
        _categoryFilter.value = category
    }

    private val _formState = MutableLiveData<MenuFormState>(MenuFormState.Idle)
    val formState: LiveData<MenuFormState> = _formState

    fun getMenuById(id: Int, onResult: (MenuEntity?) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getMenuById(id))
        }
    }

    fun saveMenu(
        id: Int,
        namaMenu: String,
        deskripsi: String,
        hargaText: String,
        gambar: String,
        kategori: String
    ) {
        if (namaMenu.isBlank() || deskripsi.isBlank() || hargaText.isBlank() || kategori.isBlank()) {
            _formState.value = MenuFormState.Error("Semua field wajib diisi")
            return
        }
        val harga = hargaText.toDoubleOrNull()
        if (harga == null || harga <= 0) {
            _formState.value = MenuFormState.Error("Harga tidak valid")
            return
        }

        _formState.value = MenuFormState.Loading
        viewModelScope.launch {
            val menu = MenuEntity(
                id = id,
                namaMenu = namaMenu,
                deskripsi = deskripsi,
                harga = harga,
                gambar = gambar,
                kategori = kategori
            )
            if (id == 0) {
                val newId = repository.insertMenu(menu).toInt()
                // Sync menu baru ke Firebase
                com.app.smartkantin.utils.FirebaseSync.sendMenu(menu.copy(id = newId))
            } else {
                repository.updateMenu(menu)
                // Sync update menu ke Firebase
                com.app.smartkantin.utils.FirebaseSync.sendMenu(menu)
            }
            _formState.value = MenuFormState.Success
        }
    }

    fun deleteMenu(menu: MenuEntity) {
        viewModelScope.launch {
            repository.deleteMenu(menu)
            // Hapus juga di Firebase
            com.app.smartkantin.utils.FirebaseSync.deleteMenu(menu.id)
        }
    }

    fun resetFormState() {
        _formState.value = MenuFormState.Idle
    }
}