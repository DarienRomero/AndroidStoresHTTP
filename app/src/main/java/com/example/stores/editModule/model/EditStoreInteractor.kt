package com.example.stores.editModule.model

import com.example.stores.StoreApplication
import com.example.stores.commonModule.entitie.StoreEntity
import java.util.concurrent.LinkedBlockingQueue

class EditStoreInteractor {
    fun saveStore(storeEntity: StoreEntity, callback: ((Long) -> Unit)){
        val queue = LinkedBlockingQueue<StoreEntity>()
        var newId: Long = 0
        Thread{
            newId = StoreApplication.database.storeDao().addStore(storeEntity)
            queue.add(storeEntity)
        }.start()

        callback(newId)
    }

    fun updateStore(storeEntity: StoreEntity, callback: ((StoreEntity) -> Unit)){
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread{
            StoreApplication.database.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
        }.start()

        callback(storeEntity)
    }
}