package com.hilmi.projekpenjualan.produk

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.content.Intent
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.hilmi.projekpenjualan.R

class DataProduk : AppCompatActivity() {

    lateinit var fabDATAPRODUKTambah : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_produk)

        init()

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

    fun init (){
        fabDATAPRODUKTambah = findViewById(R.id.btnTambahProduk)
    }
}