package com.app.smartkantin.data.dao

import androidx.room.*
import com.app.smartkantin.data.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToCart(cartItem: CartItemEntity)

    @Update
    suspend fun updateCartItem(cartItem: CartItemEntity)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: Int)

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItems(userId: Int): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE menuId = :menuId AND userId = :userId LIMIT 1")
    suspend fun getCartItemByMenuId(menuId: Int, userId: Int): CartItemEntity?
}