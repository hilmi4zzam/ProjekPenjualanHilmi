package com.hilmi.projekpenjualan.produk

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.hilmi.projekpenjualan.R

class ModProduk : AppCompatActivity() {

    private lateinit var etNama: TextInputEditText
    private lateinit var etHarga: TextInputEditText
    private lateinit var etStok: TextInputEditText
    private lateinit var spKategori: AutoCompleteTextView
    private lateinit var spCabang: AutoCompleteTextView
    private lateinit var btnSimpan: MaterialButton

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("produk")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mod_produk)

        init()

        // Setup Dropdowns
        val kategoriList = resources.getStringArray(R.array.statusKategori)
        val adapterKategori = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, kategoriList)
        spKategori.setAdapter(adapterKategori)

        val cabangList = arrayOf("Pusat", "Cabang 1", "Cabang 2")
        val adapterCabang = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cabangList)
        spCabang.setAdapter(adapterCabang)

        btnSimpan.setOnClickListener {
            simpanData()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun init() {
        etNama = findViewById(R.id.tietNamaProduk)
        etHarga = findViewById(R.id.tietHargaProduk)
        etStok = findViewById(R.id.tietStokProduk)
        spKategori = findViewById(R.id.spModKategoriProduk)
        spCabang = findViewById(R.id.spModCabangProduk)
        btnSimpan = findViewById(R.id.btnModProdukSimpan)
    }

    private fun simpanData() {
        val nama = etNama.text.toString().trim()
        val harga = etHarga.text.toString().trim()
        val stok = etStok.text.toString().trim()
        val kategori = spKategori.text.toString()
        val cabang = spCabang.text.toString()

        if (nama.isEmpty() || harga.isEmpty() || stok.isEmpty()) {
            Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        val key = myRef.push().key
        if (key != null) {
            val produkData = mapOf(
                "idProduk" to key,
                "namaProduk" to nama,
                "hargaProduk" to harga.toInt(),
                "stokProduk" to stok.toInt(),
                "idKategori" to kategori,
                "idCabang" to cabang
            )

            myRef.child(key).setValue(produkData).addOnSuccessListener {
                Toast.makeText(this, "Produk berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}