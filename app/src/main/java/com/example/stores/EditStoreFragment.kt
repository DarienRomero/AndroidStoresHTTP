package com.example.stores

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.LinkedBlockingQueue

class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null


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
        mActivity = activity as? MainActivity
        //Setea el botón de back en el AppBar
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Setea el título en el AppBar
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)
        //Setea el menú de opciones
        setHasOptionsMenu(true)


    }

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
                val store = StoreEntity(
                    name = mBinding.etName.text.toString().trim(),
                    phone = mBinding.etPhone.text.toString().trim(),
                    website = mBinding.etWebsite.text.toString().trim()
                )

                val queue = LinkedBlockingQueue<Long>()

                Thread{
                    val id = StoreApplication.database.storeDao().addStore(store)
                    queue.add(id)
                }.start()

                with(queue.take()){
                    Snackbar.make(mBinding.root, getString(R.string.edit_store_message_success), Snackbar.LENGTH_SHORT).show()
                }

                true
            }
            else -> {
                super.onOptionsItemSelected(item)

            }
        }
    }

    override fun onDestroy() {
        //Cuando se destruye el Fragment, se borra el back, se cambia el tiulo y se muestra el fab
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)
        super.onDestroy()
    }


}