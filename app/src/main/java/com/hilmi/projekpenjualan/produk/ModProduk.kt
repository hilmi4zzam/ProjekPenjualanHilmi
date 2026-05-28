package com.hilmi.projekpenjualan.produk

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.model.ModelCabang
import com.hilmi.projekpenjualan.model.ModelKategori
import com.hilmi.projekpenjualan.model.ModelProduk

class ModProduk : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tilNama: TextInputLayout
    private lateinit var tilHarga: TextInputLayout
    private lateinit var tilKategori: TextInputLayout
    private lateinit var tilStok: TextInputLayout
    private lateinit var tilCabang: TextInputLayout
    private lateinit var tilStatus: TextInputLayout
    
    private lateinit var etNama: TextInputEditText
    private lateinit var etHarga: TextInputEditText
    private lateinit var etStok: TextInputEditText
    private lateinit var spKategori: AutoCompleteTextView
    private lateinit var spCabang: AutoCompleteTextView
    private lateinit var spStatus: AutoCompleteTextView
    private lateinit var etUrlGambar: TextInputEditText
    private lateinit var ivPreview: ImageView
    private lateinit var btnSimpan: MaterialButton

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("produk")
    private val refKategori = database.getReference("kategori")
    private val refCabang = database.getReference("cabang")

    private var selectedProduk: ModelProduk? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mod_produk)

        init()

        // Setup Dropdowns from Firebase
        getKategoriFromFirebase()
        getCabangFromFirebase()

        // Setup Status Dropdown
        val statusList = resources.getStringArray(R.array.statusKategori)
        val adapterStatus = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statusList)
        spStatus.setAdapter(adapterStatus)

        // Check if editing
        selectedProduk = intent.getParcelableExtra("PRODUK")
        if (selectedProduk != null) {
            tvTitle.text = "Edit Produk"
            etUrlGambar.setText(selectedProduk?.fotoProduk)
            etNama.setText(selectedProduk?.namaProduk)
            etHarga.setText(selectedProduk?.hargaProduk.toString())
            etStok.setText(selectedProduk?.stokProduk.toString())
            spKategori.setText(selectedProduk?.idKategori, false)
            spCabang.setText(selectedProduk?.idCabang, false)
            spStatus.setText(selectedProduk?.statusProduk, false)
            // Load preview gambar langsung saat mode edit
            loadPreviewGambar(selectedProduk?.fotoProduk)
        } else {
            tvTitle.text = "Tambah Produk"
            if (statusList.isNotEmpty()) spStatus.setText(statusList[0], false)
        }

        etUrlGambar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val url = s.toString().trim()
                loadPreviewGambar(url)
            }
        })

        btnSimpan.setOnClickListener {
            val urlGambar = etUrlGambar.text.toString().trim()
            val nama = etNama.text.toString().trim()
            val harga = etHarga.text.toString().trim()
            val stok = etStok.text.toString().trim()
            val kategori = spKategori.text.toString()
            val cabang = spCabang.text.toString()
            val status = spStatus.text.toString()

            // Reset Errors
            tilNama.error = null
            tilHarga.error = null
            tilStok.error = null
            tilKategori.error = null
            tilCabang.error = null

            if (nama.isEmpty()) {
                tilNama.error = "Nama produk wajib diisi"
                etNama.requestFocus()
                return@setOnClickListener
            }
            if (harga.isEmpty()) {
                tilHarga.error = "Harga wajib diisi"
                etHarga.requestFocus()
                return@setOnClickListener
            }
            if (stok.isEmpty()) {
                tilStok.error = "Jumlah stok wajib diisi"
                etStok.requestFocus()
                return@setOnClickListener
            }
            if (kategori.isEmpty()) {
                tilKategori.error = "Kategori wajib diisi"
                spKategori.requestFocus()
                return@setOnClickListener
            }
            if (cabang.isEmpty()) {
                tilCabang.error = "Cabang wajib diisi"
                spCabang.requestFocus()
                return@setOnClickListener
            }

            val stokInt = stok.toIntOrNull() ?: 0
            if (status == "Aktif" && stokInt == 0) {
                Toast.makeText(this, "Stok habis, perbarui jumlah stok untuk mengaktifkan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val key = selectedProduk?.idProduk ?: myRef.push().key
            if (key != null) {
                val produkData = mapOf(
                    "idProduk" to key,
                    "fotoProduk" to urlGambar,
                    "namaProduk" to nama,
                    "hargaProduk" to harga.toInt(),
                    "stokProduk" to stokInt,
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

    private fun loadPreviewGambar(url: String?) {
        if (!url.isNullOrEmpty()) {
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.img_14)
                .error(R.drawable.img_14)
                .into(ivPreview)
        } else {
            ivPreview.setImageResource(R.drawable.img_14)
        }
    }

    private fun getKategoriFromFirebase() {
        refKategori.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listNamaKategori = mutableListOf<String>()
                for (data in snapshot.children) {
                    val kategori = data.getValue(ModelKategori::class.java)
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
        tvTitle = findViewById(R.id.tvTambahProduk)
        
        tilNama = findViewById(R.id.tilModProdukNama)
        tilHarga = findViewById(R.id.tilModProdukHarga)
        tilKategori = findViewById(R.id.tilModKategoriProduk)
        tilStok = findViewById(R.id.tilModProdukStok)
        tilCabang = findViewById(R.id.tilModCabangProduk)
        tilStatus = findViewById(R.id.tilModStatusProduk)

        etNama = findViewById(R.id.tietNamaProduk)
        etHarga = findViewById(R.id.tietHargaProduk)
        etStok = findViewById(R.id.tietStokProduk)
        etUrlGambar = findViewById(R.id.tietUrlGambarProduk)
        ivPreview = findViewById(R.id.previewUrlGambarProduk)
        spKategori = findViewById(R.id.spModKategoriProduk)
        spCabang = findViewById(R.id.spModCabangProduk)
        spStatus = findViewById(R.id.spModStatusProduk)
        btnSimpan = findViewById(R.id.btnModProdukSimpan)
    }
}