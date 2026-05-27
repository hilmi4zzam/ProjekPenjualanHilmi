package com.hilmi.projekpenjualan.pegawai

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hilmi.projekpenjualan.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.hilmi.projekpenjualan.model.ModelPegawai

class ModPegawai : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pegawai")

    private lateinit var tvJudul: TextView
    private lateinit var tilNamaPegawai: TextInputLayout
    private lateinit var etNamaPegawai: TextInputEditText
    private lateinit var tilStatusPegawai: TextInputLayout
    private lateinit var spStatusPegawai: AutoCompleteTextView
    private lateinit var btnSimpan: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mod_pegawai)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvJudul = findViewById(R.id.tvTambahPegawai)
        tilNamaPegawai = findViewById(R.id.tilModPegawaiNama)
        etNamaPegawai = findViewById(R.id.tietNamaPegawai)
        tilStatusPegawai = findViewById(R.id.tilModPegawaiStatus)
        spStatusPegawai = findViewById(R.id.spModPegawaiStatus)
        btnSimpan = findViewById(R.id.btnModPegawaiSimpan)

        val statusList = resources.getStringArray(R.array.statusKategori)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statusList)
        spStatusPegawai.setAdapter(adapter)

        tvJudul.text = "Tambah Pegawai"
        if (statusList.isNotEmpty()) spStatusPegawai.setText(statusList[0], false)

        btnSimpan.setOnClickListener {
            val nama = etNamaPegawai.text.toString().trim()
            val status = spStatusPegawai.text.toString()

            tilNamaPegawai.error = null

            if (nama.isEmpty()) {
                tilNamaPegawai.error = "Nama pegawai wajib diisi"
                etNamaPegawai.requestFocus()
                return@setOnClickListener
            }

            val key = myRef.push().key
            if (key != null) {
                myRef.get()
                    .addOnSuccessListener { snapshot ->
                        val hasActivePegawai = snapshot.children.any { dataPegawai ->
                            dataPegawai.child("statusPegawai").getValue(String::class.java) == "Aktif"
                        }
                        val finalStatus = if (status == "Aktif" || !hasActivePegawai) "Aktif" else "Nonaktif"
                        val updates = hashMapOf<String, Any?>()

                        if (finalStatus == "Aktif") {
                            snapshot.children.forEach { dataPegawai ->
                                val idPegawai = dataPegawai.key ?: return@forEach
                                updates["$idPegawai/statusPegawai"] = "Nonaktif"
                            }
                        }

                        updates[key] = ModelPegawai(
                            idPegawai = key,
                            namaPegawai = nama,
                            statusPegawai = finalStatus
                        )

                        myRef.updateChildren(updates)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Pegawai berhasil disimpan", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Gagal menyimpan pegawai: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal memuat data pegawai: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
