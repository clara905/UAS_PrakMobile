package com.app.smartkantin.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.smartkantin.data.dao.CartDao
import com.app.smartkantin.data.dao.MenuDao
import com.app.smartkantin.data.dao.OrderDao
import com.app.smartkantin.data.dao.OrderItemDao
import com.app.smartkantin.data.dao.PromoDao
import com.app.smartkantin.data.dao.UserDao
import com.app.smartkantin.data.entity.CartItemEntity
import com.app.smartkantin.data.entity.MenuEntity
import com.app.smartkantin.data.entity.OrderEntity
import com.app.smartkantin.data.entity.OrderItemEntity
import com.app.smartkantin.data.entity.PromoEntity
import com.app.smartkantin.data.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        MenuEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CartItemEntity::class,
        PromoEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun menuDao(): MenuDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun promoDao(): PromoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smartkantin_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}