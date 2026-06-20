package com.app.smartkantin.utils

/**
 * Role pengguna pada tabel User.
 */
object Role {
    const val ADMIN = "ADMIN"
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
    const val ADMIN_USERNAME = "admin"
    const val ADMIN_PASSWORD = "admin123"
}