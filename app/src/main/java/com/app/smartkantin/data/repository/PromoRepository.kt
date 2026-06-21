package com.app.smartkantin.data.repository

import com.app.smartkantin.data.dao.PromoDao
import com.app.smartkantin.data.entity.PromoEntity
import kotlinx.coroutines.flow.Flow

class PromoRepository(private val promoDao: PromoDao) {
    fun getAllPromos(): Flow<List<PromoEntity>> = promoDao.getAllPromos()
    suspend fun insertPromo(promo: PromoEntity) = promoDao.insertPromo(promo)
    suspend fun deletePromo(promo: PromoEntity) = promoDao.deletePromo(promo)
    suspend fun getPromoByCode(kode: String) = promoDao.getPromoByCode(kode)
}