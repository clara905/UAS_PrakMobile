package com.app.smartkantin.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_items",
    indices = [Index("orderId"), Index("menuId")]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderId: Int = 0,
    val menuId: Int = 0,
    val qty: Int = 0,
    val subtotal: Double = 0.0
)