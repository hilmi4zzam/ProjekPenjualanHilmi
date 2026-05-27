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
                    val intent = Intent(this@DataProduk, ModProduk::class.java)
                    intent.putExtra("PRODUK", produk)
                    startActivity(intent)
                }

                override fun onStatusClick(produk: ModelProduk) {
                    val statusBaru = if (produk.statusProduk == "Aktif") "Nonaktif" else "Aktif"
                    
                    if (statusBaru == "Aktif" && (produk.stokProduk ?: 0) == 0) {
                        Toast.makeText(this@DataProduk, "Stok habis, perbarui jumlah stok untuk mengaktifkan", Toast.LENGTH_SHORT).show()
                        return
                    }

                    viewModel.updateStatus(produk.idProduk, statusBaru)
                    val toastMsg = if (statusBaru == "Aktif") "Status ${produk.namaProduk} di Aktif'kan" else "Status ${produk.namaProduk} di Nonaktif'kan"
                    Toast.makeText(this@DataProduk, toastMsg, Toast.LENGTH_SHORT).show()
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

        setupSearchView()
    }

    private fun setupSearchView() {
        val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.svProduk)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterList(newText)
                return true
            }
        })
    }

    private fun showDeleteDialog(produk: ModelProduk) {
        val builder = MaterialAlertDialogBuilder(this, R.style.RoundedAlertDialog)
        builder.setTitle("Hapus Produk")
        builder.setMessage("Apakah Anda yakin ingin menghapus produk ${produk.namaProduk}?")
        builder.setPositiveButton("Hapus") { _, _ ->
            viewModel.deleteProduk(produk.idProduk)
            Toast.makeText(this, "${produk.namaProduk} berhasil dihapus", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    fun init (){
        fabDATAPRODUKTambah = findViewById(R.id.btnTambahProduk)
        rvDataProduk = findViewById(R.id.rvDataProduk)
    }
}