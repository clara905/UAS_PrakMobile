package com.app.smartkantin.utils

import com.app.smartkantin.data.entity.OrderEntity
import com.google.firebase.database.FirebaseDatabase

object FirebaseSync {
    private val database = FirebaseDatabase.getInstance(FirebaseConfig.DATABASE_URL).reference


    fun sendOrder(order: OrderEntity) {
        database.child("orders").child(order.id.toString()).setValue(order)
    }


    fun updateOrderStatus(orderId: Int, newStatus: String) {
        database.child("orders").child(orderId.toString()).child("status").setValue(newStatus)
    }


    fun sendMenu(menu: com.app.smartkantin.data.entity.MenuEntity) {
        database.child("menus").child(menu.id.toString()).setValue(menu)
    }


    fun deleteMenu(menuId: Int) {
        database.child("menus").child(menuId.toString()).removeValue()
    }


    fun sendPromo(promo: com.app.smartkantin.data.entity.PromoEntity) {
        database.child("promos").child(promo.id.toString()).setValue(promo)
    }

    fun deletePromo(promoId: Int) {
        database.child("promos").child(promoId.toString()).removeValue()
    }
}