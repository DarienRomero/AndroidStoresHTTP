package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.StoreApplication
import com.example.stores.commonModule.entitie.StoreEntity
import com.example.stores.mainModule.model.MainInteractor
import java.util.concurrent.LinkedBlockingQueue

class MainViewModel: ViewModel() {
    private var storeList: MutableList<StoreEntity>
    private var interactor: MainInteractor
    init {
        storeList =  mutableListOf()
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
            storeList = it
        }
    }

    fun deleteStore(storeEntity: StoreEntity){
        interactor.deleteStore(storeEntity, {deleted ->
            val index = storeList.indexOf(storeEntity)
            if(index != -1){
                storeList.removeAt(index)
                stores.value = storeList
            }
        })
    }
    fun updateStore(storeEntity: StoreEntity){
        storeEntity.isFavorite = !storeEntity.isFavorite
        interactor.updateStore(storeEntity, {deleted ->
            val index = storeList.indexOf(storeEntity)
            if(index != -1){
                storeList[index] = storeEntity
                stores.value = storeList
            }
        })
    }
}