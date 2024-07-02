package com.example.stores.mainModule

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.editModule.EditStoreFragment
import com.example.stores.commonModule.util.MainAux
import com.example.stores.R
import com.example.stores.StoreApplication
import com.example.stores.commonModule.entitie.StoreEntity
import com.example.stores.databinding.ActivityMainBinding
import com.example.stores.editModule.viewModel.EditStoreViewModel
import com.example.stores.mainModule.adapter.OnClickListener
import com.example.stores.mainModule.viewModel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager
    //MVVM
    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mEditViewModel: EditStoreViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.fab.setOnClickListener {
            launchEditFragment()
        }
        setupViewModel()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mMainViewModel.getStores().observe(this) {stores ->
            Log.d("EVENT observed", stores.toString())
            mAdapter.setStores(stores.toList())
        }
        mEditViewModel = ViewModelProvider(this).get(EditStoreViewModel::class.java)
        mEditViewModel.getShowFab().observe(this){ isVisible ->
            if(isVisible){
                mBinding.fab.show()
            }else{
                mBinding.fab.hide()
            }
        }
    }

    private fun launchEditFragment(args: Bundle? = null) {
        val fragment = EditStoreFragment()
        if(args != null) fragment.arguments = args
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        //Le da el comportamiento de superposicion. Habilita el botón de back
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        mEditViewModel.setShowFab(false)

    }

    private fun setupRecyclerView(){
        mAdapter = StoreAdapter(mutableListOf(), this);
        mGridLayout = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))
        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getAllStores(){
        //val queue = LinkedBlockingQueue<MutableList<StoreEntity>>()
        //Thread {
        //    val stores = StoreApplication.database.storeDao().getAllStores()
        //    queue.add(stores)
        //}.start()
        //
        //val stores = queue.take()
        //
        //mAdapter.setStores(stores)

    }

    /*
    * OnClickListener
     **/
    override fun onClick(storeId: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), storeId)

        launchEditFragment(args)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        mMainViewModel.updateStore(storeEntity)
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_options_item)
        MaterialAlertDialogBuilder(
            this
        )
            .setTitle(R.string.dialog_options_title)
            .setItems(items) { _, i ->
                when (i) {
                    0 -> confirmDelete(storeEntity)
                    1 -> dial(storeEntity.phone)
                    2 -> goToWebsite(storeEntity.website)
                }
            }
            .show()

    }

    private fun dial(phone: String){
        var callIntent = Intent().apply{
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel: $phone")
        }
        startIntent(callIntent)

    }

    private fun goToWebsite(website: String){
        if(website.isEmpty()){
            Toast.makeText(this, R.string.main_error_not_website, Toast.LENGTH_LONG).show()
        }else{
            var websiteIntent = Intent().apply{
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)

            }
            startIntent(websiteIntent)
        }

    }

    private fun startIntent(intent: Intent){
        if(intent.resolveActivity(packageManager) != null){
            startActivity(intent)
        }else{
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_LONG).show()
        }
    }

    private fun confirmDelete(storeEntity: StoreEntity){
        MaterialAlertDialogBuilder(
            this
        )
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                mMainViewModel.deleteStore(storeEntity)
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }



}