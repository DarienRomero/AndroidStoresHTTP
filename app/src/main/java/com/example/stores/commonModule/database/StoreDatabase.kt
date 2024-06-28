package com.example.stores.commonModule.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.stores.commonModule.database.StoreDao
import com.example.stores.commonModule.entitie.StoreEntity

@Database(entities = arrayOf(StoreEntity::class), version = 2)
abstract class StoreDatabase: RoomDatabase() {
    abstract fun storeDao(): StoreDao
}