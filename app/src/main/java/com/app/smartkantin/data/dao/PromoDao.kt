package com.app.smartkantin.data.dao

import androidx.room.*
import com.app.smartkantin.data.entity.PromoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PromoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromo(promo: PromoEntity): Long

    @Update
    suspend fun updatePromo(promo: PromoEntity)

    @Delete
    suspend fun deletePromo(promo: PromoEntity)

    @Query("SELECT * FROM promos ORDER BY id DESC")
    fun getAllPromos(): Flow<List<PromoEntity>>

    @Query("SELECT * FROM promos WHERE kodePromo = :kode LIMIT 1")
    suspend fun getPromoByCode(kode: String): PromoEntity?
}