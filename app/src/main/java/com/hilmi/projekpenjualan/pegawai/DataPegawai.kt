package com.hilmi.projekpenjualan.pegawai

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.adapter.AdapterPegawai
import com.hilmi.projekpenjualan.view_model.DataPegawaiViewModel
import com.hilmi.projekpenjualan.model.ModelPegawai

class DataPegawai : AppCompatActivity() {

    private val viewModel: DataPegawaiViewModel by viewModels()
    private lateinit var rvDataPegawai: RecyclerView
    private lateinit var btnTambahPegawai: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pegawai)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        rvDataPegawai.layoutManager = layoutManager
        rvDataPegawai.setHasFixedSize(true)
        
        setupSearchView()
    }

    private fun setupSearchView() {
        val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.svPegawai)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterList(newText)
                return true
            }
        })
    }

    private fun init() {
        rvDataPegawai = findViewById(R.id.rvDataPegawai)
        btnTambahPegawai = findViewById(R.id.btnTambahPegawai)

        btnTambahPegawai.setOnClickListener {
            val intent = Intent(this@DataPegawai, ModPegawai::class.java)
            startActivity(intent)
        }

        viewModel.pegawaiList.observe(this) { list ->
            val adapter = AdapterPegawai(list)
            rvDataPegawai.adapter = adapter

            adapter.setOnItemClickListener(object : AdapterPegawai.OnItemClickListener {
                override fun onItemClick(pegawai: ModelPegawai) {
                    val intent = Intent(this@DataPegawai, ModPegawai::class.java)
                    intent.putExtra("PEGAWAI", pegawai)
                    startActivity(intent)
                }

                override fun onStatusClick(pegawai: ModelPegawai) {
                    val newStatus = if (pegawai.statusPegawai == "Aktif") "Nonaktif" else "Aktif"
                    viewModel.updateStatus(pegawai.idPegawai, newStatus)
                    val toastMsg = if (newStatus == "Aktif") "Status ${pegawai.namaPegawai} di Aktif'kan" else "Status ${pegawai.namaPegawai} di Nonaktif'kan"
                    Toast.makeText(this@DataPegawai, toastMsg, Toast.LENGTH_SHORT).show()
                }

                override fun onItemLongClick(pegawai: ModelPegawai) {
                    showDeleteDialog(pegawai)
                }
            })
        }
    }

    private fun showDeleteDialog(pegawai: ModelPegawai) {
        val builder = MaterialAlertDialogBuilder(this, R.style.RoundedAlertDialog)
        builder.setTitle("Hapus Pegawai")
        builder.setMessage("\nApakah Anda yakin ingin menghapus pegawai ${pegawai.namaPegawai}?")
        builder.setPositiveButton("Hapus") { _, _ ->
            viewModel.deletePegawai(pegawai.idPegawai)
            Toast.makeText(this, "${pegawai.namaPegawai} berhasil dihapus", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}