package com.example.stores.editModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.commonModule.entitie.StoreEntity
import com.example.stores.editModule.model.EditStoreInteractor

class EditStoreViewModel : ViewModel() {
    private val storeSelected = MutableLiveData<StoreEntity>()
    private val showFab = MutableLiveData<Boolean>()
    private val result = MutableLiveData<Any>()

    private val interactor: EditStoreInteractor

    init {
        interactor = EditStoreInteractor()
    }

    fun setStoreSelected(storeEntity: StoreEntity){
        storeSelected.value = storeEntity
    }
    fun getStoreSelected() : LiveData<StoreEntity>{
        return storeSelected
    }
    fun getShowFab() : LiveData<Boolean>{
        return showFab
    }
    fun setShowFab(showFab: Boolean){
        this.showFab.value = showFab
    }

    fun getResult() : LiveData<Any>{
        return result
    }
    fun setResult(result: Any){
        this.result.value = result
    }

    fun saveStore(storeEntity: StoreEntity){
        interactor.saveStore(storeEntity){
            result.value = it
        }
    }

    fun updateStore(storeEntity: StoreEntity){
        interactor.updateStore(storeEntity){
            result.value = it
        }
    }

}