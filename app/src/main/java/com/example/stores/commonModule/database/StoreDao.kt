package com.example.stores.commonModule.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.stores.commonModule.entitie.StoreEntity

@Dao
interface StoreDao {
    @Query("SELECT * FROM  STORE_ENTITY")
    fun getAllStores(): MutableList<StoreEntity>

    @Query("SELECT * FROM STORE_ENTITY where id= :id")
    fun getStoreById(id: Long): StoreEntity
    @Insert
    fun addStore(storeEntity: StoreEntity) : Long

    @Update
    fun updateStore(storeEntity: StoreEntity)

    @Delete
    fun deleteStore(storeEntity: StoreEntity)
}