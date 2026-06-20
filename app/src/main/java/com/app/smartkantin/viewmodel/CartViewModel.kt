package com.app.smartkantin.viewmodel

import androidx.lifecycle.*
import com.app.smartkantin.data.entity.CartItemEntity
import com.app.smartkantin.data.entity.MenuEntity
import com.app.smartkantin.data.repository.CartRepository
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartRepository) : ViewModel() {

    private val _userId = MutableLiveData<Int>()
    
    // LiveData reaktif yang akan update otomatis tiap kali userId atau DB berubah
    val cartItems: LiveData<List<CartItemEntity>> = _userId.switchMap { id ->
        repository.getCartItems(id).asLiveData()
    }

    private val _checkoutResult = MutableLiveData<Boolean>()
    val checkoutResult: LiveData<Boolean> = _checkoutResult

    fun initUserId(id: Int) {
        if (_userId.value == null) {
            _userId.value = id
        }
    }

    fun addToCart(menu: MenuEntity, userId: Int) {
        viewModelScope.launch {
            repository.addToCart(menu, userId)
        }
    }

    fun updateQty(cartItem: CartItemEntity, delta: Int) {
        viewModelScope.launch {
            repository.updateQty(cartItem, delta)
        }
    }

    fun checkout(userId: Int, items: List<CartItemEntity>, total: Double) {
        viewModelScope.launch {
            _checkoutResult.value = repository.checkout(userId, items, total)
        }
    }

    fun clearCart(userId: Int) {
        viewModelScope.launch {
            repository.clearCart(userId)
        }
    }
}

class CartViewModelFactory(private val repository: CartRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}