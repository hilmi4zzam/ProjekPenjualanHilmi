package com.hilmi.projekpenjualan.laporan

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.adapter.AdapterLaporan
import com.hilmi.projekpenjualan.view_model.DataLaporanViewModel

class DataLaporanPenjualan : AppCompatActivity() {

    private val viewModel: DataLaporanViewModel by viewModels()
    private lateinit var rvLaporan: RecyclerView
    private lateinit var adapter: AdapterLaporan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_laporan_penjualan)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun initRecyclerView() {
        rvLaporan = findViewById(R.id.rvDataProduk)
        rvLaporan.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        adapter = AdapterLaporan(emptyList())
        rvLaporan.adapter = adapter
    }

    private fun setupSearchView() {
        val svLaporan = findViewById<SearchView>(R.id.svLaporan)
        svLaporan.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterList(newText)
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewModel.laporanList.observe(this) { list ->
            adapter.updateData(list)
        }
    }
}
