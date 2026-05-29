package com.hilmi.projekpenjualan

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.ImageView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
import com.hilmi.projekpenjualan.model.ModelLaporan
import java.text.NumberFormat
import java.util.Locale

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
    private lateinit var tvSaldoValue: TextView
    private val laporanRef = FirebaseDatabase.getInstance().getReference("laporan")
    private var laporanListener: ValueEventListener? = null

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

        val ivProfile = findViewById<ImageView>(R.id.ivProfile)
        ivProfile.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Keluar")
                .setMessage("Pastikan keluar sebelum menutup aplikasi")
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Keluar") { _, _ ->
                    val intent = Intent(this@MainActivity, Masuk::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .show()
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
        loadSaldoLaporan()
    }

    override fun onStop() {
        super.onStop()
        pegawaiAktifListener?.let { pegawaiRef.removeEventListener(it) }
        pegawaiAktifListener = null
        laporanListener?.let { laporanRef.removeEventListener(it) }
        laporanListener = null
    }

    fun init() {
        CardKategori = findViewById(R.id.menu2)
        CardProduk = findViewById(R.id.menu1)
        CardCabang = findViewById(R.id.menu3)
        CardTransaksi = findViewById(R.id.menu6)
        CardLaporan = findViewById(R.id.menu5)
        CardPegawai = findViewById(R.id.menu4)
        tvHaloPegawaiAktif = findViewById(R.id.haihilmi)
        tvSaldoValue = findViewById(R.id.tvSaldoValue)
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

    private fun loadSaldoLaporan() {
        if (laporanListener != null) return

        laporanListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalSaldo = 0
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val laporan = dataSnapshot.getValue(ModelLaporan::class.java)
                        if (laporan != null) {
                            totalSaldo += laporan.totalHarga ?: 0
                        }
                    }
                }
                val localeID = Locale("in", "ID")
                val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                tvSaldoValue.text = formatRupiah.format(totalSaldo)
            }

            override fun onCancelled(error: DatabaseError) {
                tvSaldoValue.text = "Rp 0"
            }
        }

        laporanRef.addValueEventListener(laporanListener!!)
    }
}
