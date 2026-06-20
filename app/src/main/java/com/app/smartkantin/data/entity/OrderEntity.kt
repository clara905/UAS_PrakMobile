package com.app.smartkantin.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val totalHarga: Double,
    val status: String, // MENUNGGU / DIPROSES / SELESAI — lihat utils.OrderStatus
    val tanggal: String // disimpan sebagai String format "yyyy-MM-dd HH:mm"
)