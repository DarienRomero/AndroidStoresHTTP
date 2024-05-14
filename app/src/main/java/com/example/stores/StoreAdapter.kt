package com.example.stores

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stores.databinding.ItemStoreBinding

class StoreAdapter(private val stores: MutableList<Store>, private val listener: OnClickListener) : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ItemStoreBinding.bind(view)

        fun setListener(store: Store, position: Int){
            binding.root.setOnClickListener { listener.onClick(store, position) }
        }
    }

    //Bindea el UserAdapter.kt con el item_user.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_store, parent, false)
        return ViewHolder(view)
    }

    //Retorna la longitud de la lista a iterar
    override fun getItemCount(): Int {

        return stores.size
    }

    //Hace la modificacion del xml (Por cada elemento) antes de renderizar
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("POSITION", position.toString())
        val store = stores[position]
        val humanPosition = position + 1
        with(holder){
            setListener(store, humanPosition)
            binding.tvName.text = store.name
        }
    }

    fun remove(position: Int) {
        stores.removeAt(position)
        notifyItemRemoved(position)
    }

    fun add(store: Store) {
        stores.add(store)
        notifyDataSetChanged()
    }
}