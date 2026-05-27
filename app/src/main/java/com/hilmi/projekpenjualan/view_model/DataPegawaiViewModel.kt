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
                    normalizeActivePegawai(snapshot)
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

    fun updateStatus(
        pegawai: ModelPegawai,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val idPegawai = pegawai.idPegawai
        if (idPegawai == null) {
            onError("Data pegawai tidak valid")
            return
        }

        myRef.get()
            .addOnSuccessListener { snapshot ->
                val updates = hashMapOf<String, Any?>()

                if (pegawai.statusPegawai == "Aktif") {
                    return@addOnSuccessListener
                }

                updates["$idPegawai/statusPegawai"] = "Aktif"
                snapshot.children.forEach { dataPegawai ->
                    val currentId = dataPegawai.key ?: return@forEach
                    if (currentId != idPegawai) {
                        updates["$currentId/statusPegawai"] = "Nonaktif"
                    }
                }

                myRef.updateChildren(updates)
                    .addOnSuccessListener {
                        Log.d("DataPegawaiViewModel", "Status updated: $idPegawai is active")
                        onSuccess("Status ${pegawai.namaPegawai} di Aktif'kan")
                    }
                    .addOnFailureListener {
                        Log.e("DataPegawaiViewModel", "Failed to update status: ${it.message}")
                        onError("Gagal memperbarui status: ${it.message}")
                    }
            }
            .addOnFailureListener {
                Log.e("DataPegawaiViewModel", "Failed to load employees: ${it.message}")
                onError("Gagal memuat data pegawai: ${it.message}")
            }
    }

    fun deletePegawai(
        pegawai: ModelPegawai,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val idPegawai = pegawai.idPegawai
        if (idPegawai == null) {
            onError("Data pegawai tidak valid")
            return
        }

        myRef.get()
            .addOnSuccessListener { snapshot ->
                val otherPegawai = snapshot.children.filter { it.key != idPegawai }
                val updates = hashMapOf<String, Any?>()

                if (pegawai.statusPegawai == "Aktif" && otherPegawai.isNotEmpty()) {
                    val nextActiveId = otherPegawai.first().key
                    if (nextActiveId != null) {
                        updates["$nextActiveId/statusPegawai"] = "Aktif"
                    }
                } else if (pegawai.statusPegawai == "Aktif") {
                    onError("Minimal harus ada satu pegawai aktif")
                    return@addOnSuccessListener
                }
                updates[idPegawai] = null

                myRef.updateChildren(updates)
                    .addOnSuccessListener {
                        Log.d("DataPegawaiViewModel", "Pegawai deleted successfully")
                        onSuccess()
                    }
                    .addOnFailureListener {
                        Log.e("DataPegawaiViewModel", "Failed to delete pegawai: ${it.message}")
                        onError("Gagal menghapus pegawai: ${it.message}")
                    }
            }
            .addOnFailureListener {
                Log.e("DataPegawaiViewModel", "Failed to load employees: ${it.message}")
                onError("Gagal memuat data pegawai: ${it.message}")
            }
    }

    private fun normalizeActivePegawai(snapshot: DataSnapshot) {
        val pegawaiSnapshots = snapshot.children.toList()
        if (pegawaiSnapshots.isEmpty()) return

        val activePegawai = pegawaiSnapshots.filter { dataPegawai ->
            dataPegawai.child("statusPegawai").getValue(String::class.java) == "Aktif"
        }
        val activeIdToKeep = activePegawai.firstOrNull()?.key ?: pegawaiSnapshots.firstOrNull()?.key ?: return
        val updates = hashMapOf<String, Any?>()

        pegawaiSnapshots.forEach { dataPegawai ->
            val idPegawai = dataPegawai.key ?: return@forEach
            val currentStatus = dataPegawai.child("statusPegawai").getValue(String::class.java)
            val expectedStatus = if (idPegawai == activeIdToKeep) "Aktif" else "Nonaktif"
            if (currentStatus != expectedStatus) {
                updates["$idPegawai/statusPegawai"] = expectedStatus
            }
        }

        if (updates.isNotEmpty()) {
            myRef.updateChildren(updates)
                .addOnSuccessListener {
                    Log.d("DataPegawaiViewModel", "Active employee status normalized")
                }
                .addOnFailureListener {
                    Log.e("DataPegawaiViewModel", "Failed to normalize active employee: ${it.message}")
                }
        }
    }
}
