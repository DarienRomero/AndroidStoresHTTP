package com.example.stores

import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.LinkedBlockingQueue

class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private var mStoreEntity: StoreEntity? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return mBinding.root
    }
    //Ocurre después del onCreateView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(getString(R.string.arg_id), 0)
        if(id != null && id != 0L){
            mIsEditMode = true
            getStore(id)
            Toast.makeText(activity, id.toString(), Toast.LENGTH_SHORT).show()
        }else{
            mIsEditMode = false
            mStoreEntity = StoreEntity(
                name = "",
                photoUrl = "",
                phone = "",
                website = ""
            )
        }

       mActivity = activity as? MainActivity
        //Setea el botón de back en el AppBar
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Setea el título en el AppBar
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)
        //Setea el menú de opciones
        setHasOptionsMenu(true)
        mBinding.etPhotoUrl.addTextChangedListener {
            Glide.with(this)
                .load(mBinding.etPhotoUrl.text.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imgPhoto)
        }


    }

    private fun getStore(id: Long) {
        val queue = LinkedBlockingQueue<StoreEntity?>()
        Thread{
            mStoreEntity = StoreApplication.database.storeDao().getStoreById(id)
            queue.add(mStoreEntity)
        }.start()
        queue.take()?.let{
            setUiStore(it)
        }
    }

    private fun setUiStore(storeEntity: StoreEntity) {
        with(mBinding){
            etName.text = storeEntity.name.editable()
            etPhone.text = storeEntity.phone.editable()
            etPhone.setText(storeEntity.phone)
            etWebsite.setText(storeEntity.website)
            etPhotoUrl.setText(storeEntity.photoUrl)
            //La imagen se cambia cuando cambia etPhotoUrl, revisar listener
        }
    }

    private fun String.editable() : Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //Une el menú lógico con el recurso
        inflater.inflate(R.menu.menu_save, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            //Cuando presiona el botón a la izquierda, en este caso, el back
            android.R.id.home -> {
                mActivity?.onBackPressedDispatcher?.onBackPressed()
                    true
            }
            R.id.action_save -> {
                //Cuando presiona una de las opciones del menú

                if(mStoreEntity != null){
                    with(mStoreEntity!!){
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }

                    val queue = LinkedBlockingQueue<StoreEntity>()

                    Thread{
                        if(mIsEditMode){
                            StoreApplication.database.storeDao().updateStore(mStoreEntity!!)
                        }else{
                            mStoreEntity!!.id = StoreApplication.database.storeDao().addStore(mStoreEntity!!)
                        }

                        queue.add(mStoreEntity)
                    }.start()

                    with(queue.take()){
                        if(mIsEditMode){
                            mActivity?.updateStore(this)
                            Toast.makeText(mActivity, getString(R.string.edit_store_edit_message_success), Toast.LENGTH_SHORT).show()
                        }else{
                            mActivity?.addStore(this)
                            Toast.makeText(mActivity, getString(R.string.edit_store_message_success), Toast.LENGTH_SHORT).show()
                        }

                        hideKeyboard()
                        //Snackbar.make(mBinding.root, getString(R.string.edit_store_message_success), Snackbar.LENGTH_SHORT).show()

                        mActivity?.onBackPressedDispatcher?.onBackPressed()
                    }
                }



                true
            }
            else -> {
                super.onOptionsItemSelected(item)

            }
        }
    }

    private fun hideKeyboard(){
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
    //Se ejecuta antes que onDestroy
    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        //Cuando se destruye el Fragment, se borra el back, se cambia el tiulo y se muestra el fab
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)
        super.onDestroy()
    }


}