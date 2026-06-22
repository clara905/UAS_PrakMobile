package com.app.smartkantin.data.repository

import com.app.smartkantin.data.dao.MenuDao
import com.app.smartkantin.data.entity.MenuEntity
import kotlinx.coroutines.flow.Flow

class MenuRepository(private val menuDao: MenuDao) {

    fun getAllMenu(): Flow<List<MenuEntity>> = menuDao.getAllMenu()

    fun getMenuByCategory(kategori: String): Flow<List<MenuEntity>> = menuDao.getMenuByCategory(kategori)

    fun searchMenu(keyword: String): Flow<List<MenuEntity>> = menuDao.searchMenu(keyword)

    suspend fun getMenuById(id: Int): MenuEntity? = menuDao.getMenuById(id)

    suspend fun upsertMenu(menu: MenuEntity) = menuDao.upsertMenu(menu)

    suspend fun insertMenu(menu: MenuEntity): Long = menuDao.insertMenu(menu)

    suspend fun updateMenu(menu: MenuEntity) = menuDao.updateMenu(menu)

    suspend fun deleteMenu(menu: MenuEntity) = menuDao.deleteMenu(menu)
}