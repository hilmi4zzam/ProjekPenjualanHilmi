package com.hilmi.projekpenjualan.kategori

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.adapter.AdapterKategori
import com.hilmi.projekpenjualan.model.DataKategoriViewModel
import com.hilmi.projekpenjualan.model.ModelKategori
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DataKategori : AppCompatActivity() {

    private val viewModel: DataKategoriViewModel by viewModels()
    private lateinit var rvDATAKATEGORI: RecyclerView
    private lateinit var fabDATAKATEGORITambah: FloatingActionButton
    private lateinit var tvkosong: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_kategori)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        rvDATAKATEGORI.layoutManager = layoutManager
        rvDATAKATEGORI.setHasFixedSize(true)
    }

    private fun init() {
        rvDATAKATEGORI = findViewById(R.id.rvDataKategori)
        fabDATAKATEGORITambah = findViewById(R.id.btnTambahKategori)

        fabDATAKATEGORITambah.setOnClickListener {
            val intent = Intent(this@DataKategori, ModKategori::class.java)
            startActivity(intent)
        }

        viewModel.kategoriList.observe(this) { list ->
            val adapter = AdapterKategori(list)
            rvDATAKATEGORI.adapter = adapter

            adapter.setOnItemClickListener(object : AdapterKategori.OnItemClickListener {
                override fun onItemClick(kategori: ModelKategori) {
                    if (!kategori.idKategori.isNullOrBlank()) {
                        showKategoriDetail(kategori)
                    } else {
                        Toast.makeText(
                            this@DataKategori,
                            "ID kategori kosong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }

    //dontol

    private fun showKategoriDetail(kategori: ModelKategori) {
        Toast.makeText(this, "Klik: ${kategori.namaKategori}", Toast.LENGTH_SHORT).show()
    }
}