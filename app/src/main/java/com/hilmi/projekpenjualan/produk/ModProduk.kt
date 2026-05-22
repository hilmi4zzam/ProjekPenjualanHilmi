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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.model.ModelCabang
import com.hilmi.projekpenjualan.model.ModelKategori

class ModProduk : AppCompatActivity() {

    private lateinit var etNama: TextInputEditText
    private lateinit var etHarga: TextInputEditText
    private lateinit var etStok: TextInputEditText
    private lateinit var spKategori: AutoCompleteTextView
    private lateinit var spCabang: AutoCompleteTextView
    private lateinit var spStatus: AutoCompleteTextView
    private lateinit var btnSimpan: MaterialButton

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("produk")
    private val refKategori = database.getReference("kategori")
    private val refCabang = database.getReference("cabang")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mod_produk)

        init()

        // Setup Dropdowns from Firebase to spinner auto complete
        getKategoriFromFirebase()
        getCabangFromFirebase()

        // Setup Status Dropdown
        val statusList = resources.getStringArray(R.array.statusKategori)
        val adapterStatus = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statusList)
        spStatus.setAdapter(adapterStatus)
        if (statusList.isNotEmpty()) spStatus.setText(statusList[0], false)

        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val harga = etHarga.text.toString().trim()
            val stok = etStok.text.toString().trim()
            val kategori = spKategori.text.toString()
            val cabang = spCabang.text.toString()
            val status = spStatus.text.toString()

            if (nama.isEmpty()) {
                etNama.error = "Nama cabang wajib diisi"
                return@setOnClickListener
            }
            if (harga.isEmpty()) {
                etHarga.error = "Harga wajib diisi"
                return@setOnClickListener
            }
            if (stok.isEmpty()) {
                etStok.error = "Jumlah stok wajib diisi"
                return@setOnClickListener
            }
            if (kategori.isEmpty()) {
                spKategori.error = "Kategori wajib diisi"
                return@setOnClickListener
            }
            if (cabang.isEmpty()) {
                spCabang.error = "Cabang wajib diisi"
                return@setOnClickListener
            }

            val key = myRef.push().key
            if (key != null) {
                val produkData = mapOf(
                    "idProduk" to key,
                    "namaProduk" to nama,
                    "hargaProduk" to harga.toInt(),
                    "stokProduk" to stok.toInt(),
                    "idKategori" to kategori,
                    "idCabang" to cabang,
                    "statusProduk" to status
                )

                myRef.child(key).setValue(produkData).addOnSuccessListener {
                    Toast.makeText(this, "Produk berhasil disimpan", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Gagal menyimpan: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //buat ambil data dari firebase untuk ditampilkan ke spinner auto complete(tak tandai make merah merah)
    private fun getKategoriFromFirebase() {
        refKategori.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listNamaKategori = mutableListOf<String>()
                for (data in snapshot.children) {
                    val kategori = data.getValue(ModelKategori::class.java)
                    // Hanya tampilkan kategori yang statusnya "Aktif"
                    if (kategori?.statusKategori == "Aktif") {
                        kategori.namaKategori?.let { listNamaKategori.add(it) }
                    }
                }
                val adapter = ArrayAdapter(this@ModProduk, android.R.layout.simple_dropdown_item_1line, listNamaKategori)
                spKategori.setAdapter(adapter)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ModProduk, "Gagal memuat kategori: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCabangFromFirebase() {
        refCabang.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listNamaCabang = mutableListOf<String>()
                for (data in snapshot.children) {
                    val cabang = data.getValue(ModelCabang::class.java)
                    // Hanya tampilkan cabang yang statusnya "Aktif"
                    if (cabang?.statusCabang == "Aktif") {
                        cabang.namaCabang?.let { listNamaCabang.add(it) }
                    }
                }
                val adapter = ArrayAdapter(this@ModProduk, android.R.layout.simple_dropdown_item_1line, listNamaCabang)
                spCabang.setAdapter(adapter)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ModProduk, "Gagal memuat cabang: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun init() {
        etNama = findViewById(R.id.tietNamaProduk)
        etHarga = findViewById(R.id.tietHargaProduk)
        etStok = findViewById(R.id.tietStokProduk)
        spKategori = findViewById(R.id.spModKategoriProduk)
        spCabang = findViewById(R.id.spModCabangProduk)
        spStatus = findViewById(R.id.spModStatusProduk)
        btnSimpan = findViewById(R.id.btnModProdukSimpan)
    }
}