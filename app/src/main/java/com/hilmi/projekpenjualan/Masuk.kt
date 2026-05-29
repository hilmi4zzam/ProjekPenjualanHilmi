package com.hilmi.projekpenjualan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class Masuk : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_masuk)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tilModMasuk = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilModMasuk)
        val tilModMasukPassword = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilModMasukPassword)
        val tietMasuk = findViewById<TextInputEditText>(R.id.tietMasuk)
        val tietMasukPassword = findViewById<TextInputEditText>(R.id.tietMasukPassword)
        val btnLanjutkan = findViewById<MaterialButton>(R.id.btnLanjutkan)

        btnLanjutkan.setOnClickListener {
            val username = tietMasuk.text.toString().trim()
            val password = tietMasukPassword.text.toString().trim()

            tilModMasuk.error = null
            tilModMasukPassword.error = null

            var isValid = true

            if (username.isEmpty()) {
                tilModMasuk.error = "Nama pengguna wajib diisi"
                isValid = false
            }

            if (password.isEmpty()) {
                tilModMasukPassword.error = "Kata sandi wajib diisi"
                isValid = false
            }

            if (!isValid) {
                if (username.isEmpty()) tietMasuk.requestFocus()
                else if (password.isEmpty()) tietMasukPassword.requestFocus()
                return@setOnClickListener
            }

            if (username == "kasirin aku dong" && password == "12345678") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Nama Pengguna atau Kata Sandi salah", Toast.LENGTH_SHORT).show()
            }
        }
    }
}