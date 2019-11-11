package com.example.teste.contactsFeature

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teste.R
import com.example.teste.adapter.AdapterRC
import com.example.teste.data.remote.Resources
import com.example.teste.databinding.ActivityContactsBinding
import kotlinx.android.synthetic.main.activity_contacts.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ContactsActivity : AppCompatActivity() {
    private val contactsViewModel: ContactsViewModel by viewModel()
    private val binding: ActivityContactsBinding by inject {
        parametersOf(
            this,
            R.layout.activity_contacts
        )
    }
    private val adapter = AdapterRC()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        setBinding()
        setAdapter()
        setClickAdapter()
        setObservables()
        focusController()
        changeColorText()
        contactsViewModel.requestUsers()
    }

    private fun setBinding() {
        binding.viewModel = contactsViewModel
        binding.lifecycleOwner = this
    }

    private fun setAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setObservables() {
        requestListObservable()
        filterSearchView()
    }
    fun setClickAdapter(){
        adapter.onItemClick = {
            Toast.makeText(this,it.name,Toast.LENGTH_LONG).show()
        }
    }

    private fun requestListObservable() {
        contactsViewModel.listUser.observe(this, Observer {
            when (it.status) {
                Resources.StatusRequest.SUCCES -> {
                    contactsViewModel.loading = false
                    it.data?.let { list ->
                        adapter.setValues(list.toMutableList())
                        adapter.notifyDataSetChanged()
                    }
                }
                Resources.StatusRequest.ERROR -> {
                    contactsViewModel.loading = false
                }
                Resources.StatusRequest.LOADING -> {
                    contactsViewModel.loading = true
                }
            }
        })
    }

    private fun filterSearchView() {
        searchView.setOnQueryTextListener(
            object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return true
                }
            }
        )
    }

    private fun focusController() {
        searchView.setOnQueryTextFocusChangeListener { _, select ->
            if (select) {
                changeFocusSearchView(true)
            } else {
                changeFocusSearchView(false)

            }
        }
    }

    private fun changeFocusSearchView(focus: Boolean) {
        val searchIcon: ImageView =
            searchView.findViewById((androidx.appcompat.R.id.search_mag_icon))
        val searchIconX: ImageView =
            searchView.findViewById((androidx.appcompat.R.id.search_close_btn))
        if (focus) {
            searchView.background =
                ContextCompat.getDrawable(baseContext, R.drawable.ic_launcher_background)

            searchIcon.setColorFilter(
                ResourcesCompat.getColor(resources, R.color.whinte, null),
                PorterDuff.Mode.SRC_ATOP
            )
            searchIconX.setColorFilter(
                ResourcesCompat.getColor(resources, R.color.whinte, null),
                PorterDuff.Mode.SRC_ATOP
            )
            searchView.background =
                ContextCompat.getDrawable(baseContext, R.drawable.field_focus)
        } else {
            searchIconX.clearColorFilter()
            searchIcon.clearColorFilter()
            searchView.background =
                ContextCompat.getDrawable(baseContext, R.drawable.field_no_focus)
        }
    }

    private fun changeColorText() {
        val color = ContextCompat.getColor(this,R.color.grayText)
        val textColorSearchView: EditText =
            searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        textColorSearchView.setTextColor(color)
        textColorSearchView.setHintTextColor(color)
    }
}
