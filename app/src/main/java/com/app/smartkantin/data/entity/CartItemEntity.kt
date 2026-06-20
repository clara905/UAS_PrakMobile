package com.app.smartkantin.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val menuId: Int,
    val namaMenu: String,
    val harga: Double,
    val gambar: String,
    val qty: Int, // Ganti jadi val biar aman
    val userId: Int
)