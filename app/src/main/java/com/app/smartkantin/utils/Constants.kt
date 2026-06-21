package com.app.smartkantin.utils

/**
 * Role pengguna pada tabel User.
 */
object Role {
    const val PENJUAL = "PENJUAL"
    const val PEMBELI = "PEMBELI"
}

/**
 * Status pesanan pada tabel Order.
 */
object OrderStatus {
    const val MENUNGGU = "MENUNGGU"
    const val DIPROSES = "DIPROSES"
    const val SELESAI = "SELESAI"
}

object DefaultAccount {
    const val PENJUAL_EMAIL = "penjual@gmail.com"
    const val PENJUAL_PASSWORD = "penjual123"
}

object FirebaseConfig {
    const val DATABASE_URL = "https://smartkantin-91402-default-rtdb.asia-southeast1.firebasedatabase.app"
}