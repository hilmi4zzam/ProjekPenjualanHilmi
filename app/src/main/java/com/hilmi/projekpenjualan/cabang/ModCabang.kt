package com.hilmi.projekpenjualan.cabang

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

class ModCabang : AppCompatActivity() {

    private lateinit var etNama: TextInputEditText
    private lateinit var spStatus: AutoCompleteTextView
    private lateinit var btnSimpan: MaterialButton

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("cabang")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mod_cabang)

        init()

        // Setup Dropdown Status
        val statusList = resources.getStringArray(R.array.statusKategori)
        val adapterStatus = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statusList)
        spStatus.setAdapter(adapterStatus)
        if (statusList.isNotEmpty()) spStatus.setText(statusList[0], false)

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
        etNama = findViewById(R.id.tietNamaCabang)
        spStatus = findViewById(R.id.spModCabangStatus)
        btnSimpan = findViewById(R.id.btnModCabangSimpan)
    }

    private fun simpanData() {
        val nama = etNama.text.toString().trim()
        val status = spStatus.text.toString()

        if (nama.isEmpty()) {
            etNama.error = "Nama cabang wajib diisi"
            return
        }

        val key = myRef.push().key
        if (key != null) {
            val cabangData = mapOf(
                "idCabang" to key,
                "namaCabang" to nama,
                "statusCabang" to status
            )

            myRef.child(key).setValue(cabangData).addOnSuccessListener {
                Toast.makeText(this, "Cabang berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
