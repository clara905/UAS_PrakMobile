package com.app.smartkantin.data.model

data class FirebaseOrder(
    val id: Int = 0,
    val userId: Int = 0,
    val totalHarga: Double = 0.0,
    val status: String = "",
    val tanggal: String = ""
)