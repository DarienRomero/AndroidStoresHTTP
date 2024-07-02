package com.example.stores.commonModule.util

import com.example.stores.commonModule.entitie.StoreEntity

interface MainAux {
    fun hideFab(isVisible: Boolean = false)
    fun addStore(storeEntity: StoreEntity, mIsEditMode: Boolean)
}