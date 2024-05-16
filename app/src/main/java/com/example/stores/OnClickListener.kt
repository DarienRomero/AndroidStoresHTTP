package com.example.stores

interface OnClickListener {
    fun onClick(storeEntity: StoreEntity, position: Int)
    fun onFavoriteStore(storeEntity: StoreEntity)
    fun onDeleteStore(storeEntity: StoreEntity)
}

