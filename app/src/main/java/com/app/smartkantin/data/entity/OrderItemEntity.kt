package com.app.smartkantin.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MenuEntity::class,
            parentColumns = ["id"],
            childColumns = ["menuId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId"), Index("menuId")]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderId: Int,
    val menuId: Int,
    val qty: Int,
    val subtotal: Double
)