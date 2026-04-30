package com.hilmi.projekpenjualan

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hilmi.projekpenjualan.kategori.DataKategori

class MainActivity : AppCompatActivity() {

    lateinit var CardKategori : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        init()

        // 2. Setting agar Icon (Jam, Baterai, Sinyal) berwarna HITAM/GELAP
        val warnaIconStatusBar = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
        warnaIconStatusBar.isAppearanceLightStatusBars = true
        setContentView(R.layout.activity_main)

        CardKategori.setOnClickListener {
            val intent = Intent(this@MainActivity, DataKategori::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
    }

    fun init() {
        CardKategori = findViewById(R.id.menu2)
    }
}