package com.hilmi.projekpenjualan.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hilmi.projekpenjualan.model.ModelPegawai

class DataPegawaiViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pegawai")
    val pegawaiList = MutableLiveData<ArrayList<ModelPegawai>>()
    private var originalPegawaiList = ArrayList<ModelPegawai>()
    private val searchQuery = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()
    val isSearchEmpty = MutableLiveData<Boolean>()

    init {
        getData()
    }

    fun getData() {
        isLoading.value = true
        val query = myRef.orderByChild("idPegawai").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isLoading.value = false
                if (snapshot.exists()) {
                    val list = ArrayList<ModelPegawai>()
                    for (dataSnapshot in snapshot.children) {
                        val pegawai = dataSnapshot.getValue(ModelPegawai::class.java)
                        if (pegawai == null) {
                            Log.e("DataPegawaiViewModel", "Failed to parse pegawai data for snapshot: ${dataSnapshot.key}")
                        } else {
                            list.add(pegawai)
                        }
                    }
                    originalPegawaiList.clear()
                    originalPegawaiList.addAll(list)
                    pegawaiList.value = list
                    isSearchEmpty.value = false
                    Log.d("DataPegawaiViewModel", "Loaded ${list.size} pegawai items.")
                } else {
                    originalPegawaiList.clear()
                    pegawaiList.value = ArrayList()
                    isSearchEmpty.value = true
                    Log.d("DataPegawaiViewModel", "No pegawai data found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading.value = false
            }
        })
    }

    fun filterList(query: String?) {
        searchQuery.value = query
        if (query.isNullOrEmpty()) {
            pegawaiList.value = originalPegawaiList
            isSearchEmpty.value = false
        } else {
            val filteredList = originalPegawaiList.filter {
                it.namaPegawai?.lowercase()?.contains(query.lowercase()) == true
            }
            pegawaiList.value = ArrayList(filteredList)
            isSearchEmpty.value = filteredList.isEmpty()
        }
    }

    fun updateStatus(idPegawai: String?, newStatus: String) {
        if (idPegawai != null) {
            myRef.child(idPegawai).child("statusPegawai").setValue(newStatus)
                .addOnSuccessListener {
                    Log.d("DataPegawaiViewModel", "Status updated to $newStatus")
                }
                .addOnFailureListener {
                    Log.e("DataPegawaiViewModel", "Failed to update status: ${it.message}")
                }
        }
    }

    fun deletePegawai(idPegawai: String?) {
        if (idPegawai != null) {
            myRef.child(idPegawai).removeValue()
                .addOnSuccessListener {
                    Log.d("DataPegawaiViewModel", "Pegawai deleted successfully")
                }
                .addOnFailureListener {
                    Log.e("DataPegawaiViewModel", "Failed to delete pegawai: ${it.message}")
                }
        }
    }
}