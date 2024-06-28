package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.StoreApplication
import com.example.stores.commonModule.entitie.StoreEntity
import java.util.concurrent.LinkedBlockingQueue

class MainViewModel: ViewModel() {
    private var stores: MutableLiveData<List<StoreEntity>>
    init {
        stores = MutableLiveData()
        loadStores()
    }

    fun getStores() : LiveData<List<StoreEntity>> {
        return stores
    }

    private fun loadStores(){
        val queue = LinkedBlockingQueue<MutableList<StoreEntity>>()
        Thread {
            val stores = StoreApplication.database.storeDao().getAllStores()
            queue.add(stores)
        }.start()

        val stores = queue.take()

        this.stores.value = stores
    }
}