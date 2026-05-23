package com.hilmi.projekpenjualan.transaksi

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.adapter.AdapterTransaction
import com.hilmi.projekpenjualan.view_model.DataProdukViewModel
import java.text.NumberFormat
import java.util.Locale

class DataTransaksi : AppCompatActivity() {

    private val viewModel: DataProdukViewModel by viewModels()
    private lateinit var adapter: AdapterTransaction
    private lateinit var rvDataProduk: RecyclerView
    private lateinit var tvTotalHarga: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_transaksi)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvTotalHarga = findViewById(R.id.tvTotalHarga)
        
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

        adapter.setOnQuantityChangeListener(object : AdapterTransaction.OnQuantityChangeListener {
            override fun onQuantityChanged(totalPrice: Int) {
                updateTotalHarga(totalPrice)
            }
        })
    }

    private fun updateTotalHarga(totalPrice: Int) {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        tvTotalHarga.text = formatRupiah.format(totalPrice)
    }

    private fun observeViewModel() {
        viewModel.produkList.observe(this) { list ->
            // Menampilkan produk yang statusnya "Aktif"
            val activeProducts = list.filter { it.statusProduk == "Aktif" }
            adapter.updateData(activeProducts)
            updateTotalHarga(adapter.getTotalPrice())
        }
    }
}
