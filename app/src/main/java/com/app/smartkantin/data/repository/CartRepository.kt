package com.app.smartkantin.data.repository

import com.app.smartkantin.data.dao.CartDao
import com.app.smartkantin.data.dao.OrderDao
import com.app.smartkantin.data.dao.OrderItemDao
import com.app.smartkantin.data.entity.CartItemEntity
import com.app.smartkantin.data.entity.MenuEntity
import com.app.smartkantin.data.entity.OrderEntity
import com.app.smartkantin.data.entity.OrderItemEntity
import com.app.smartkantin.utils.OrderStatus
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartRepository(
    private val cartDao: CartDao,
    private val orderDao: OrderDao? = null,
    private val orderItemDao: OrderItemDao? = null
) {

    fun getCartItems(userId: Int): Flow<List<CartItemEntity>> = cartDao.getCartItems(userId)

    suspend fun addToCart(menu: MenuEntity, userId: Int) {
        val existing = cartDao.getCartItemByMenuId(menu.id, userId)
        if (existing != null) {
            // Gunakan .copy() untuk membuat objek baru, jangan edit existing.qty langsung
            val updated = existing.copy(qty = existing.qty + 1)
            cartDao.updateCartItem(updated)
        } else {
            val newItem = CartItemEntity(
                menuId = menu.id,
                namaMenu = menu.namaMenu,
                harga = menu.harga,
                gambar = menu.gambar,
                qty = 1,
                userId = userId
            )
            cartDao.insertToCart(newItem)
        }
    }

    suspend fun checkout(userId: Int, items: List<CartItemEntity>, total: Double): Boolean {
        if (orderDao == null || orderItemDao == null) return false
        
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val orderId = orderDao.insertOrder(
            OrderEntity(
                userId = userId,
                totalHarga = total,
                status = OrderStatus.MENUNGGU,
                tanggal = date
            )
        ).toInt()

        // Sync ke Firebase setelah simpan lokal berhasil
        val newOrder = orderDao.getOrderById(orderId)
        newOrder?.let { com.app.smartkantin.utils.FirebaseSync.sendOrder(it) }

        val orderItems = items.map {
            OrderItemEntity(
                orderId = orderId,
                menuId = it.menuId,
                qty = it.qty,
                subtotal = it.harga * it.qty
            )
        }
        orderItemDao.insertOrderItems(orderItems)
        cartDao.clearCart(userId)
        return true
    }

    suspend fun updateQty(cartItem: CartItemEntity, delta: Int) {
        val newQty = cartItem.qty + delta
        if (newQty > 0) {
            // Gunakan .copy() di sini juga
            val updated = cartItem.copy(qty = newQty)
            cartDao.updateCartItem(updated)
        } else {
            cartDao.deleteCartItem(cartItem)
        }
    }

    suspend fun clearCart(userId: Int) {
        cartDao.clearCart(userId)
    }
}