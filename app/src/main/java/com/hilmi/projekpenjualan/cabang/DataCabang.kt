package com.hilmi.projekpenjualan.cabang

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.adapter.AdapterCabang
import com.hilmi.projekpenjualan.model.ModelCabang
import com.hilmi.projekpenjualan.view_model.DataCabangViewModel

class DataCabang : AppCompatActivity() {

    private val viewModel: DataCabangViewModel by viewModels()
    private lateinit var rvCabang: RecyclerView
    private lateinit var btnTambah: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_cabang)

        init()

        rvCabang.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }

        viewModel.cabangList.observe(this) { list ->
            val adapter = AdapterCabang(list)
            rvCabang.adapter = adapter

            adapter.setOnItemClickListener(object : AdapterCabang.OnItemClickListener {
                override fun onItemClick(cabang: ModelCabang) {
                    // Implementasi detail/edit jika perlu
                }

                override fun onStatusClick(cabang: ModelCabang) {
                    val newStatus = if (cabang.statusCabang == "Aktif") "Nonaktif" else "Aktif"
                    viewModel.updateStatus(cabang.idCabang, newStatus)
                    Toast.makeText(this@DataCabang, "Status ${cabang.namaCabang} di$newStatus'kan ", Toast.LENGTH_SHORT).show()
                }

                override fun onItemLongClick(cabang: ModelCabang) {
                    showDeleteDialog(cabang)
                }
            })
        }

        btnTambah.setOnClickListener {
            startActivity(Intent(this, ModCabang::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupSearchView()
    }

    private fun setupSearchView() {
        val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.svCabang)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterList(newText)
                return true
            }
        })
    }

    private fun init() {
        rvCabang = findViewById(R.id.rvDataCabang)
        btnTambah = findViewById(R.id.btnTambahCabang)
    }

    private fun showDeleteDialog(cabang: ModelCabang) {
        val builder = MaterialAlertDialogBuilder(this, R.style.RoundedAlertDialog)
        builder.setTitle("Hapus Cabang")
        builder.setMessage("Apakah Anda yakin ingin menghapus cabang ${cabang.namaCabang}?")
        builder.setPositiveButton("Hapus") { _, _ ->
            viewModel.deleteCabang(cabang.idCabang)
            Toast.makeText(this, "${cabang.namaCabang} berhasil dihapus", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}
