package com.hilmi.projekpenjualan.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.hilmi.projekpenjualan.model.ModelCabang

class DataCabangViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("cabang")
    val cabangList = MutableLiveData<ArrayList<ModelCabang>>()
    private var originalCabangList = ArrayList<ModelCabang>()
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
                val list = ArrayList<ModelCabang>()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val cabang = dataSnapshot.getValue(ModelCabang::class.java)
                        if (cabang != null) {
                            list.add(cabang)
                        }
                    }
                    originalCabangList.clear()
                    originalCabangList.addAll(list)
                    cabangList.value = list
                    isSearchEmpty.value = false
                } else {
                    originalCabangList.clear()
                    cabangList.value = ArrayList()
                    isSearchEmpty.value = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading.value = false
                Log.e("DataCabangViewModel", "Error: ${error.message}")
            }
        })
    }

    fun filterList(query: String?) {
        if (query.isNullOrEmpty()) {
            cabangList.value = originalCabangList
            isSearchEmpty.value = false
        } else {
            val filteredList = originalCabangList.filter {
                it.namaCabang?.lowercase()?.contains(query.lowercase()) == true
            }
            cabangList.value = ArrayList(filteredList)
            isSearchEmpty.value = filteredList.isEmpty()
        }
    }

    fun updateStatus(idCabang: String?, newStatus: String) {
        if (idCabang != null) {
            myRef.child(idCabang).child("statusCabang").setValue(newStatus)
        }
    }

    fun deleteCabang(idCabang: String?) {
        if (idCabang != null) {
            myRef.child(idCabang).removeValue()
        }
    }
}
