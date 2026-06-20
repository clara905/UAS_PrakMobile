package com.app.smartkantin.viewmodel

import androidx.lifecycle.*
import com.app.smartkantin.data.dao.OrderDao
import com.app.smartkantin.data.entity.OrderEntity
import com.app.smartkantin.utils.OrderStatus
import kotlinx.coroutines.launch

class OrderViewModel(private val orderDao: OrderDao) : ViewModel() {

    fun getOrdersByUser(userId: Int): LiveData<List<OrderEntity>> {
        return orderDao.getOrdersByUser(userId).asLiveData()
    }

    fun getAllOrders(): LiveData<List<OrderEntity>> {
        return orderDao.getAllOrders().asLiveData()
    }

    fun updateStatus(orderId: Int, currentStatus: String) {
        val nextStatus = when (currentStatus) {
            OrderStatus.MENUNGGU -> OrderStatus.DIPROSES
            OrderStatus.DIPROSES -> OrderStatus.SELESAI
            else -> currentStatus
        }
        viewModelScope.launch {
            orderDao.updateStatus(orderId, nextStatus)
        }
    }
}

class OrderViewModelFactory(private val orderDao: OrderDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(orderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}