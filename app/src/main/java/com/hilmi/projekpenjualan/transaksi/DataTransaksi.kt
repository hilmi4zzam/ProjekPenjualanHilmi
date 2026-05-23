package com.hilmi.projekpenjualan.transaksi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.adapter.AdapterTransaction
import com.hilmi.projekpenjualan.model.ModelProduk
import com.hilmi.projekpenjualan.view_model.DataProdukViewModel

class DataTransaksi : AppCompatActivity() {

    private val viewModel: DataProdukViewModel by viewModels()
    private lateinit var adapter: AdapterTransaction
    private lateinit var rvDataProduk: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_transaksi)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initRecyclerView()
        observeViewModel()
        setupSearchView()
    }

    private fun setupSearchView() {
        val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.svDataProduk)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterList(newText)
                return true
            }
        })
    }

    private fun initRecyclerView() {
        rvDataProduk = findViewById(R.id.rvDataProduk)
        rvDataProduk.layoutManager = LinearLayoutManager(this)
        
        adapter = AdapterTransaction(emptyList())
        rvDataProduk.adapter = adapter

        adapter.setOnItemClickListener(object : AdapterTransaction.OnItemClickListener {
            override fun onAddClick(produk: ModelProduk) {
                // Logic for adding product to transaction can be added here
                Toast.makeText(this@DataTransaksi, "Ditambahkan: ${produk.namaProduk}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun observeViewModel() {
        viewModel.produkList.observe(this) { list ->
            // Menampilkan produk yang statusnya "Aktif"
            val activeProducts = list.filter { it.statusProduk == "Aktif" }
            adapter.updateData(activeProducts)
        }
    }
}
