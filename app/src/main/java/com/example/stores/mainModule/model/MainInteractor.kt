package com.example.stores.mainModule.model

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.stores.StoreApplication
import com.example.stores.commonModule.entitie.StoreEntity
import com.example.stores.commonModule.util.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.LinkedBlockingQueue

class MainInteractor {
    val url = Constants.STORES_URL + Constants.GET_ALL_PATH

    fun getStores(callback: ((MutableList<StoreEntity>) -> Unit)){
        val jsonObjectResponse = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            Log.i("Response", response.toString())
            //val status = response.getInt(Constants.STATUS_PROPERTY)
            val status = response.optInt(Constants.STATUS_PROPERTY, Constants.ERROR)
            if(status == Constants.SUCCESS){
                Log.i("status", status.toString())
                //val jsonObject = Gson().fromJson(response.getJSONArray(Constants.STORES_PROPERTY).get(0).toString(), StoreEntity::class.java)
                val jsonArray = response.optJSONArray(Constants.STORES_PROPERTY)
                if(jsonArray != null){
                    val jsonList = jsonArray.get(0).toString()
                    val mutableListType = object : TypeToken<MutableList<StoreEntity>>(){

                    }.type
                    val storeList = Gson().fromJson<MutableList<StoreEntity>>(jsonList, mutableListType)
                    callback(storeList)
                }

            }
        }, {
            it.printStackTrace()
        })

        StoreApplication.storeAPI.addToRequestQueue(jsonObjectResponse)
    }
    fun deleteStore(storeEntity: StoreEntity, callback: ((StoreEntity) -> Unit)){
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            queue.add(storeEntity)
        }.start()
        callback(queue.take())
    }

    fun addStore(storeEntity: StoreEntity, mIsEditMode: Boolean, callback: ((StoreEntity) -> Unit)){
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread{
            if(mIsEditMode){
                StoreApplication.database.storeDao().updateStore(storeEntity)
            }else{
                storeEntity.id = StoreApplication.database.storeDao().addStore(storeEntity)
            }

            queue.add(storeEntity)
        }.start()

        callback(queue.take())
    }

    fun updateStore(storeEntity: StoreEntity, callback: ((StoreEntity) -> Unit)){
        storeEntity.isFavorite = !storeEntity.isFavorite
        val queue = LinkedBlockingQueue<StoreEntity>()

        Thread {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
        }.start()
        callback(queue.take())
    }


    fun getStoresRoom(callback: ((MutableList<StoreEntity>) -> Unit)){
        val queue = LinkedBlockingQueue<MutableList<StoreEntity>>()
        Thread {
            val stores = StoreApplication.database.storeDao().getAllStores()
            queue.add(stores)
        }.start()
        val stores = queue.take()
        val json = Gson().toJson(stores)
        callback(stores)
    }
}