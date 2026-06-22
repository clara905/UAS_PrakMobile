package com.app.smartkantin.utils

import android.net.Uri
import com.app.smartkantin.data.entity.OrderEntity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

object FirebaseSync {
    private val database = FirebaseDatabase.getInstance(FirebaseConfig.DATABASE_URL).reference
    private val storage = FirebaseStorage.getInstance().reference

    suspend fun uploadImage(uriString: String): String {
        if (uriString.isBlank()) return ""
        if (uriString.startsWith("http")) return uriString // Link internet langsung pakai
        
        return try {
            val uri = Uri.parse(uriString)
            val fileName = "menu_${System.currentTimeMillis()}.jpg"
            val ref = storage.child("menu_images/$fileName")
            
            val uploadTask = ref.putFile(uri)
            uploadTask.await() 
            
            val downloadUrl = ref.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            // Jika gagal upload (misal storage belum aktif), kembalikan uri asli
            // supaya tetap bisa tampil di HP pengunggah.
            uriString
        }
    }

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