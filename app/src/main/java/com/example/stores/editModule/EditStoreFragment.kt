package com.example.stores.editModule

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
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.R
import com.example.stores.commonModule.entitie.StoreEntity
import com.example.stores.databinding.FragmentEditStoreBinding
import com.example.stores.editModule.viewModel.EditStoreViewModel
import com.example.stores.mainModule.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.util.concurrent.LinkedBlockingQueue

class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private lateinit var mStoreEntity: StoreEntity
    //MVVM
    private lateinit var mEditStoreViewModel: EditStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEditStoreViewModel = ViewModelProvider(requireActivity()).get(EditStoreViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return mBinding.root
    }
    //Ocurre después del onCreateView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //MVVM
        setupViewModel()
        setupTextFields()
    }

    private fun setupViewModel() {
        mEditStoreViewModel.getStoreSelected().observe(viewLifecycleOwner){
            mStoreEntity = it
            if(it.id != 0L){
                mIsEditMode = true
                setUiStore(it)
            }else{
                mIsEditMode = false
            }
            setupActionBar()
        }
        mEditStoreViewModel.getResult().observe(viewLifecycleOwner){result ->
            hideKeyboard()

            when(result){
                is Long -> {
                    mStoreEntity.id = result
                    mEditStoreViewModel.setStoreSelected(mStoreEntity)
                    Toast.makeText(mActivity, getString(R.string.edit_store_edit_message_success), Toast.LENGTH_SHORT).show()
                }
                is StoreEntity -> {
                    mEditStoreViewModel.setStoreSelected(mStoreEntity)
                    Toast.makeText(mActivity, getString(R.string.edit_store_message_success), Toast.LENGTH_SHORT).show()
                }
            }
            //requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        //Setea el botón de back en el AppBar
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Setea el título en el AppBar
        mActivity?.supportActionBar?.title = if(mIsEditMode) getString(R.string.edit_store_title_edit) else getString(
            R.string.edit_store_title_add
        )
        //Setea el menú de opciones
        setHasOptionsMenu(true)
    }

    private fun setupTextFields() {
        with(mBinding){
            etPhotoUrl.addTextChangedListener {
                validateFields(mBinding.tilPhotoUrl)
                loadImage()
            }
            etName.addTextChangedListener {
                validateFields(mBinding.tilName)
            }
            etPhone.addTextChangedListener {
                validateFields(mBinding.tilPhone)
            }
        }

    }

    private fun loadImage(){
        Glide.with(this)
            .load(String)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.imgPhoto)
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

    override fun onAttach(context: Context) {
        requireActivity().onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.dialog_exit_title)
                    .setMessage(R.string.dialog_exit_message)
                    .setPositiveButton(R.string.dialog_exit_ok){ _, _ ->
                        if(isEnabled){
                            isEnabled = false
                            mActivity?.onBackPressedDispatcher?.onBackPressed()
                        }

                    }
                    .setNegativeButton(R.string.dialog_delete_cancel, null)
                    .show()
            }
        })
        super.onAttach(context)
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

                if(validateFields(mBinding.tilPhone, mBinding.tilName, mBinding.tilPhotoUrl)) {
                    with(mStoreEntity){
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }

                    val queue = LinkedBlockingQueue<StoreEntity>()

                    Thread{
                        if(mIsEditMode){
                            mEditStoreViewModel.updateStore(mStoreEntity)
                        }else{
                            mEditStoreViewModel.saveStore(mStoreEntity)
                        }

                        queue.add(mStoreEntity)
                    }.start()


                }



                true
            }
            else -> {
                super.onOptionsItemSelected(item)

            }
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout) : Boolean {
        var isValid = true
        for(textField in textFields){
            if(textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.helper_required)
                textField.editText?.requestFocus()
                isValid = false
            }else{
                textField.error = null
            }
        }
        if(!isValid) {
            Snackbar.make(mBinding.root, R.string.edit_store_message_invalid, Snackbar.LENGTH_SHORT).show()
        }
        return isValid
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
        mEditStoreViewModel.setResult(Any())

        mEditStoreViewModel.setShowFab(true)
        super.onDestroy()
    }


}