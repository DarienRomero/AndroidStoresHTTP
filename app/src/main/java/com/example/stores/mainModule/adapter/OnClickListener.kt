package com.example.stores.mainModule.adapter

import com.example.stores.commonModule.entitie.StoreEntity

interface OnClickListener {
    fun onClickListItem(storeEntity: StoreEntity)
    fun onFavoriteStore(storeEntity: StoreEntity)
    fun onDeleteStore(storeEntity: StoreEntity)
}

