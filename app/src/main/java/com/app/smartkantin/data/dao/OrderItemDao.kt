package com.app.smartkantin.data.dao

import androidx.room.*
import com.app.smartkantin.data.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {

    @Insert
    suspend fun insertOrderItem(item: OrderItemEntity): Long

    @Insert
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Delete
    suspend fun deleteOrderItem(item: OrderItemEntity)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getItemsByOrder(orderId: Int): Flow<List<OrderItemEntity>>

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteItemsByOrder(orderId: Int)
}