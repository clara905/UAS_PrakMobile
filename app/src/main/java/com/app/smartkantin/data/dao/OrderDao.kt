package com.app.smartkantin.data.dao

import androidx.room.*
import com.app.smartkantin.data.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateStatus(orderId: Int, status: String)

    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Int): OrderEntity?

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY id DESC")
    fun getOrdersByUser(userId: Int): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY id DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>
}