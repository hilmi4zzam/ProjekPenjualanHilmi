package com.hilmi.projekpenjualan.transaksi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.adapter.AdapterTransaksi
import com.hilmi.projekpenjualan.view_model.DataProdukViewModel
import java.text.NumberFormat
import java.util.Locale

class DataTransaksi : AppCompatActivity() {

    private val viewModel: DataProdukViewModel by viewModels()
    private lateinit var adapter: AdapterTransaksi
    private lateinit var rvDataProduk: RecyclerView
    private lateinit var tvTotalHarga: TextView
    private lateinit var btnReset: CardView
    private lateinit var btnPesan: CardView

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
        btnReset = findViewById(R.id.btnReset)
        btnPesan = findViewById(R.id.btnPesan)
        
        btnReset.setOnClickListener {
            adapter.resetQuantities()
        }

        btnPesan.setOnClickListener {
            val selectedItems = ArrayList(adapter.getSelectedItems())
            if (selectedItems.isNotEmpty()) {
                val intent = Intent(this, ModTransaksi::class.java)
                intent.putParcelableArrayListExtra("SELECTED_ITEMS", selectedItems)
                intent.putExtra("TOTAL_PRICE", adapter.getTotalPrice())
                startActivity(intent)
            }
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
        
        adapter = AdapterTransaksi(emptyList())
        rvDataProduk.adapter = adapter

        adapter.setOnQuantityChangeListener(object : AdapterTransaksi.OnQuantityChangeListener {
            override fun onQuantityChanged(totalPrice: Int) {
                updateTotalHarga(totalPrice)
                // Tampilkan tombol reset jika total harga > 0 (artinya ada produk yang dipilih)
                btnReset.visibility = if (totalPrice > 0) View.VISIBLE else View.GONE
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
            val totalPrice = adapter.getTotalPrice()
            updateTotalHarga(totalPrice)
            btnReset.visibility = if (totalPrice > 0) View.VISIBLE else View.GONE
        }
    }
}
