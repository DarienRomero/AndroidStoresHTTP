package com.example.stores

import android.app.Application
import androidx.room.Room

//DOC: Se declara en el Manifest
class StoreApplication: Application() {
    //object _> singleton, companion -> static
    companion object {
        lateinit var database: StoreDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            StoreDatabase::class.java,
            "StoreDatabase")
            .build()
    }
}