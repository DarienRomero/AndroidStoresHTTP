package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.StoreApplication
import com.example.stores.commonModule.entitie.StoreEntity
import com.example.stores.mainModule.model.MainInteractor
import java.util.concurrent.LinkedBlockingQueue

class MainViewModel: ViewModel() {

    private var interactor: MainInteractor
    init {
        interactor = MainInteractor()
    }

    fun getStores() : LiveData<List<StoreEntity>> {
        return stores.also{
            loadStores()
        }
    }

    private val stores: MutableLiveData<List<StoreEntity>> by lazy {
        MutableLiveData<List<StoreEntity>>()
    }

    private fun loadStores(){
//        interactor.getStoresCallback(object: MainInteractor.StoresCallback{
//            override fun getStoresCallback(callback: MutableList<StoreEntity>) {
//                stores.value = callback
//            }
//
//        })

        interactor.getStores {
            stores.value = it
        }


    }
}