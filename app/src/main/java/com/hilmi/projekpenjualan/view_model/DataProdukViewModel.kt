package com.hilmi.projekpenjualan.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hilmi.projekpenjualan.model.ModelProduk

class DataProdukViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("produk")
    val produkList = MutableLiveData<ArrayList<ModelProduk>>()
    private var originalProdukList = ArrayList<ModelProduk>()
    
    private var currentQuery: String? = null
    private var currentCategory: String? = null
    
    val isLoading = MutableLiveData<Boolean>()
    val isSearchEmpty = MutableLiveData<Boolean>()

    init {
        getData()
    }

    fun getData() {
        isLoading.value = true
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isLoading.value = false
                val list = ArrayList<ModelProduk>()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val produk = dataSnapshot.getValue(ModelProduk::class.java)
                        if (produk != null) {
                            list.add(produk)
                        }
                    }
                    originalProdukList.clear()
                    originalProdukList.addAll(list)
                    applyFilters()
                } else {
                    originalProdukList.clear()
                    produkList.value = ArrayList()
                    isSearchEmpty.value = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading.value = false
                Log.e("DataProdukViewModel", "Error: ${error.message}")
            }
        })
    }

    fun filterList(query: String?) {
        currentQuery = query
        applyFilters()
    }
    
    fun filterByCategory(category: String?) {
        currentCategory = category
        applyFilters()
    }
    
    private fun applyFilters() {
        var filteredList = originalProdukList.toList()
        
        // Filter by search query
        if (!currentQuery.isNullOrEmpty()) {
            filteredList = filteredList.filter {
                it.namaProduk?.lowercase()?.contains(currentQuery!!.lowercase()) == true
            }
        }
        
        // Filter by category
        if (!currentCategory.isNullOrEmpty()) {
            filteredList = filteredList.filter {
                it.idKategori == currentCategory
            }
        }
        
        produkList.value = ArrayList(filteredList)
        isSearchEmpty.value = filteredList.isEmpty()
    }

    fun deleteProduk(idProduk: String?) {
        if (idProduk != null) {
            myRef.child(idProduk).removeValue()
        }
    }

    fun updateStatus(idProduk: String?, newStatus: String) {
        if (idProduk != null) {
            myRef.child(idProduk).child("statusProduk").setValue(newStatus)
        }
    }
}
