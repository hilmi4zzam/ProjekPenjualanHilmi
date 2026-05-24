package com.hilmi.projekpenjualan.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hilmi.projekpenjualan.model.ModelLaporan

class DataLaporanViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("laporan")
    val laporanList = MutableLiveData<ArrayList<ModelLaporan>>()
    private var originalLaporanList = ArrayList<ModelLaporan>()

    init {
        getData()
    }

    private fun getData() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<ModelLaporan>()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val laporan = dataSnapshot.getValue(ModelLaporan::class.java)
                        if (laporan != null) {
                            list.add(laporan)
                        }
                    }
                    originalLaporanList.clear()
                    originalLaporanList.addAll(list)
                    laporanList.value = list
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun filterList(query: String?) {
        if (query.isNullOrEmpty()) {
            laporanList.value = originalLaporanList
        } else {
            val filteredList = originalLaporanList.filter {
                it.namaPemesan?.lowercase()?.contains(query.lowercase()) == true
            }
            laporanList.value = ArrayList(filteredList)
        }
    }
}
