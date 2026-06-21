package com.app.smartkantin.viewmodel

import androidx.lifecycle.*
import com.app.smartkantin.data.entity.PromoEntity
import com.app.smartkantin.data.repository.PromoRepository
import kotlinx.coroutines.launch

class PromoViewModel(private val repository: PromoRepository) : ViewModel() {

    val allPromos: LiveData<List<PromoEntity>> = repository.getAllPromos().asLiveData()

    private val _promoApplied = MutableLiveData<PromoEntity?>()
    val promoApplied: LiveData<PromoEntity?> = _promoApplied

    fun applyPromo(kode: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val promo = repository.getPromoByCode(kode)
            if (promo != null) {
                _promoApplied.value = promo
                onResult(null)
            } else {
                onResult("Kode promo tidak valid")
            }
        }
    }

    fun addPromo(kode: String, deskripsi: String, persen: Int) {
        viewModelScope.launch {
            repository.insertPromo(
                PromoEntity(kodePromo = kode, deskripsi = deskripsi, persenPotongan = persen, minBelanja = 0.0)
            )
        }
    }

    fun deletePromo(promo: PromoEntity) {
        viewModelScope.launch {
            repository.deletePromo(promo)
        }
    }
}

class PromoViewModelFactory(private val repository: PromoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PromoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PromoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}