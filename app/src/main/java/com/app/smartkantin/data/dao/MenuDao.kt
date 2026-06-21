package com.app.smartkantin.data.dao

import androidx.room.*
import com.app.smartkantin.data.entity.MenuEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {

    @Insert
    suspend fun insertMenu(menu: MenuEntity): Long

    @Update
    suspend fun updateMenu(menu: MenuEntity)

    @Delete
    suspend fun deleteMenu(menu: MenuEntity)

    @Query("SELECT * FROM menu WHERE id = :id")
    suspend fun getMenuById(id: Int): MenuEntity?

    @Query("SELECT * FROM menu ORDER BY namaMenu ASC")
    fun getAllMenu(): Flow<List<MenuEntity>>

    @Query("SELECT * FROM menu WHERE kategori = :kategori ORDER BY namaMenu ASC")
    fun getMenuByCategory(kategori: String): Flow<List<MenuEntity>>

    @Query("SELECT * FROM menu WHERE namaMenu LIKE '%' || :keyword || '%' ORDER BY namaMenu ASC")
    fun searchMenu(keyword: String): Flow<List<MenuEntity>>
}