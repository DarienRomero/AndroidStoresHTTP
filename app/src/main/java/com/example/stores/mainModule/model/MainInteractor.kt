package com.example.stores.mainModule.model

import com.example.stores.StoreApplication
import com.example.stores.commonModule.entitie.StoreEntity
import java.util.concurrent.LinkedBlockingQueue

class MainInteractor {

    interface StoresCallback {
        fun getStoresCallback(callback: MutableList<StoreEntity>)
    }

    fun getStoresCallback(callback: StoresCallback){
        val queue = LinkedBlockingQueue<MutableList<StoreEntity>>()
        Thread {
            val stores = StoreApplication.database.storeDao().getAllStores()
            queue.add(stores)
        }.start()

        val stores = queue.take()
        callback.getStoresCallback(stores)
    }

    fun getStores(callback: ((MutableList<StoreEntity>) -> Unit)){
        val queue = LinkedBlockingQueue<MutableList<StoreEntity>>()
        Thread {
            val stores = StoreApplication.database.storeDao().getAllStores()
            queue.add(stores)
        }.start()

        val stores = queue.take()
        callback(stores)
    }
}