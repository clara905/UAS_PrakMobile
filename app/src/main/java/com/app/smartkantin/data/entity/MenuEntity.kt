package com.app.smartkantin.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu")
data class MenuEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val namaMenu: String = "",
    val deskripsi: String = "",
    val harga: Double = 0.0,
    val gambar: String = "", // path lokal / uri gambar menu
    val kategori: String = ""
)