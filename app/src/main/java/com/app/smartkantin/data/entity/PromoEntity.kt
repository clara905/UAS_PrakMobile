package com.app.smartkantin.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "promos")
data class PromoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val kodePromo: String = "",
    val deskripsi: String = "",
    val persenPotongan: Int = 0,
    val minBelanja: Double = 0.0
)