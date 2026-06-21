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
    val userId: Int = 0,
    val totalHarga: Double = 0.0,
    val status: String = "",
    val tanggal: String = ""
)