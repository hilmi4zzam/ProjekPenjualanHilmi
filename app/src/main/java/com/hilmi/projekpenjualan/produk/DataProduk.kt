package com.hilmi.projekpenjualan.produk

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.content.Intent
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.adapter.AdapterProduk
import com.hilmi.projekpenjualan.model.ModelProduk
import com.hilmi.projekpenjualan.view_model.DataProdukViewModel

class DataProduk : AppCompatActivity() {

    private val viewModel: DataProdukViewModel by viewModels()
    private lateinit var fabDATAPRODUKTambah : MaterialButton
    private lateinit var rvDataProduk: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_produk)

        init()

        rvDataProduk.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }

        viewModel.produkList.observe(this) { list ->
            val adapter = AdapterProduk(list)
            rvDataProduk.adapter = adapter
            
            adapter.setOnItemClickListener(object : AdapterProduk.OnItemClickListener {
                override fun onItemClick(produk: ModelProduk) {
                    // Implementasi edit jika diperlukan
                }

                override fun onItemLongClick(produk: ModelProduk) {
                    showDeleteDialog(produk)
                }
            })
        }

        fabDATAPRODUKTambah.setOnClickListener {
            val intent = Intent(this@DataProduk, ModProduk::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showDeleteDialog(produk: ModelProduk) {
        MaterialAlertDialogBuilder(this, R.style.WhiteMaterialAlertDialog)
            .setTitle("Hapus Produk")
            .setMessage("Apakah Anda yakin ingin menghapus ${produk.namaProduk}?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteProduk(produk.idProduk)
                Toast.makeText(this, "${produk.namaProduk} berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    fun init (){
        fabDATAPRODUKTambah = findViewById(R.id.btnTambahProduk)
        rvDataProduk = findViewById(R.id.rvDataProduk)
    }
}