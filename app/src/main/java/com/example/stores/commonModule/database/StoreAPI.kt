package com.example.stores.commonModule.database

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.Request
import com.android.volley.toolbox.Volley

class StoreAPI constructor(context: Context){

    //Miembros estáticos
    companion object{
        //Volatile significa que puede ser modificada
        //por cualquier hilo y el cambio se verá
        //en todos los hilos
        @Volatile
        private var INSTANCE: StoreAPI? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this){
            INSTANCE ?: StoreAPI(context).also {
                INSTANCE = it
            }
        }
    }

    val requestQueu: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>){
        requestQueu.add(req)
    }
}