package com.hilmi.projekpenjualan

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hilmi.projekpenjualan.cabang.DataCabang
import com.hilmi.projekpenjualan.kategori.DataKategori
import com.hilmi.projekpenjualan.laporan.DataLaporanPenjualan
import com.hilmi.projekpenjualan.pegawai.DataPegawai
import com.hilmi.projekpenjualan.produk.DataProduk
import com.hilmi.projekpenjualan.transaksi.DataTransaksi

class MainActivity : AppCompatActivity() {

    lateinit var CardKategori : ConstraintLayout
    lateinit var CardProduk : ConstraintLayout
    lateinit var CardCabang : ConstraintLayout
    lateinit var CardTransaksi : ConstraintLayout
    lateinit var CardLaporan : ConstraintLayout
    lateinit var CardPegawai : ConstraintLayout
    private lateinit var tvHaloPegawaiAktif: TextView
    private val pegawaiRef = FirebaseDatabase.getInstance().getReference("pegawai")
    private var pegawaiAktifListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        init()

        CardKategori.setOnClickListener {
            val intent = Intent(this@MainActivity, DataKategori::class.java)
            startActivity(intent)
        }

        CardProduk.setOnClickListener {
            val intent = Intent(this@MainActivity, DataProduk::class.java)
            startActivity(intent)
        }

        CardCabang.setOnClickListener {
            val intent = Intent(this@MainActivity, DataCabang::class.java)
            startActivity(intent)
        }

        CardTransaksi.setOnClickListener {
            val intent = Intent(this@MainActivity, DataTransaksi::class.java)
            startActivity(intent)
        }

        CardLaporan.setOnClickListener {
            val intent = Intent(this@MainActivity, DataLaporanPenjualan::class.java)
            startActivity(intent)
        }

        CardPegawai.setOnClickListener {
            val intent = Intent(this@MainActivity, DataPegawai::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
    }

    override fun onStart() {
        super.onStart()
        loadPegawaiAktif()
    }

    override fun onStop() {
        super.onStop()
        pegawaiAktifListener?.let { pegawaiRef.removeEventListener(it) }
        pegawaiAktifListener = null
    }

    fun init() {
        CardKategori = findViewById(R.id.menu2)
        CardProduk = findViewById(R.id.menu1)
        CardCabang = findViewById(R.id.menu3)
        CardTransaksi = findViewById(R.id.menu6)
        CardLaporan = findViewById(R.id.menu5)
        CardPegawai = findViewById(R.id.menu4)
        tvHaloPegawaiAktif = findViewById(R.id.haihilmi)
    }

    private fun loadPegawaiAktif() {
        if (pegawaiAktifListener != null) return

        pegawaiAktifListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val namaPegawaiAktif = snapshot.children.firstOrNull { dataPegawai ->
                    dataPegawai.child("statusPegawai").getValue(String::class.java) == "Aktif"
                }?.child("namaPegawai")?.getValue(String::class.java)

                tvHaloPegawaiAktif.text = if (namaPegawaiAktif.isNullOrBlank()) {
                    "Halo"
                } else {
                    "Halo $namaPegawaiAktif"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                tvHaloPegawaiAktif.text = "Halo"
            }
        }

        pegawaiRef.addValueEventListener(pegawaiAktifListener!!)
    }
}
