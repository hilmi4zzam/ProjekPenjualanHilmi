package com.hilmi.projekpenjualan.transaksi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.model.CartItem
import java.text.NumberFormat
import java.util.Locale

class ModTransaksi : AppCompatActivity() {

    private lateinit var llItemSummary: LinearLayout
    private lateinit var tvTotalHarga: TextView
    private lateinit var rbQrisButton: RadioButton
    private lateinit var rbTunaiButton: RadioButton
    private lateinit var tilNamaPemesan: TextInputLayout
    private lateinit var tietNamaPemesan: TextInputEditText
    private lateinit var tilDibayar: TextInputLayout
    private lateinit var tietDibayar: TextInputEditText
    private lateinit var btnPesan: CardView
    private var totalPrice: Int = 0
    private var selectedItems: ArrayList<CartItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mod_transaksi)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        llItemSummary = findViewById(R.id.llItemSummary)
        tvTotalHarga = findViewById(R.id.tvTotalHarga)
        rbQrisButton = findViewById(R.id.rbQrisButton)
        rbTunaiButton = findViewById(R.id.rbTunaiButton)
        tilNamaPemesan = findViewById(R.id.tilModKategoriNama)
        tietNamaPemesan = findViewById(R.id.tietNamaKategori)
        tilDibayar = findViewById(R.id.tilModDibayar)
        tietDibayar = findViewById(R.id.tietModDibayar)
        btnPesan = findViewById(R.id.btnPesan)

        selectedItems = intent.getParcelableArrayListExtra<CartItem>("SELECTED_ITEMS")
        totalPrice = intent.getIntExtra("TOTAL_PRICE", 0)

        displayItems(selectedItems)
        displayTotalPrice(totalPrice)

        setupPaymentSelection()
        setupBayarButton()
    }

    private fun setupPaymentSelection() {
        rbQrisButton.setOnClickListener {
            rbTunaiButton.isChecked = false
        }
        rbTunaiButton.setOnClickListener {
            rbQrisButton.isChecked = false
        }
        
        findViewById<View>(R.id.rbQris).setOnClickListener {
            rbQrisButton.isChecked = true
            rbTunaiButton.isChecked = false
        }
        findViewById<View>(R.id.rbTunai).setOnClickListener {
            rbTunaiButton.isChecked = true
            rbQrisButton.isChecked = false
        }
    }

    private fun setupBayarButton() {
        btnPesan.setOnClickListener {
            val nama = tietNamaPemesan.text.toString().trim()
            if (nama.isEmpty()) {
                tilNamaPemesan.error = "Nama pemesan wajib diisi"
                tietNamaPemesan.requestFocus()
                return@setOnClickListener
            } else {
                tilNamaPemesan.error = null
            }

            val dibayar = tietDibayar.text.toString().trim()
            if (dibayar.isEmpty()) {
                tilDibayar.error = "Nominal dibayar wajib diisi"
                tietDibayar.requestFocus()
                return@setOnClickListener
            } else {
                tilDibayar.error = null
            }

            val jumlahDibayar = dibayar.toLongOrNull() ?: 0L
            if (jumlahDibayar < totalPrice) {
                Toast.makeText(this, "Uang dibayarkan kurang", Toast.LENGTH_SHORT).show()
                tietDibayar.requestFocus()
                return@setOnClickListener
            }

            when {
                rbQrisButton.isChecked -> showQrisPopup(jumlahDibayar)
                rbTunaiButton.isChecked -> {
                    navigateToNota(jumlahDibayar)
                }
                else -> {
                    Toast.makeText(this, "Silakan pilih metode pembayaran", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showQrisPopup(jumlahDibayar: Long) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_popup_qris, null)
        val dialog = MaterialAlertDialogBuilder(this, R.style.RoundedAlertDialog)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvTotalHargaQris = dialogView.findViewById<TextView>(R.id.tvTotalHargaQris)
        val btnLanjutkan = dialogView.findViewById<MaterialButton>(R.id.btnLanjutkan)

        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        tvTotalHargaQris.text = formatRupiah.format(totalPrice).replace("Rp", "").trim()

        btnLanjutkan.setOnClickListener {
            dialog.dismiss()
            navigateToNota(jumlahDibayar)
        }

        dialog.show()
    }

    private fun navigateToNota(jumlahDibayar: Long) {
        val intent = Intent(this, Nota::class.java)
        intent.putParcelableArrayListExtra("SELECTED_ITEMS", selectedItems)
        intent.putExtra("TOTAL_PRICE", totalPrice)
        intent.putExtra("NAMA_PEMESAN", tietNamaPemesan.text.toString().trim())
        intent.putExtra("DIBAYAR", jumlahDibayar)
        startActivity(intent)
        finish()
    }

    private fun displayItems(items: ArrayList<CartItem>?) {
        llItemSummary.removeAllViews()
        if (items.isNullOrEmpty()) return

        val inflater = LayoutInflater.from(this)
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        for (item in items) {
            val itemView = inflater.inflate(R.layout.layout_item_summary, llItemSummary, false)
            val tvItemName = itemView.findViewById<TextView>(R.id.tvItemName)
            val tvItemPrice = itemView.findViewById<TextView>(R.id.tvItemPrice)

            tvItemName.text = "${item.namaProduk} - ${item.jumlah}x"
            val pricePerItem = (item.hargaProduk ?: 0) * (item.jumlah ?: 0)
            tvItemPrice.text = formatRupiah.format(pricePerItem)

            llItemSummary.addView(itemView)
        }
    }

    private fun displayTotalPrice(total: Int) {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        tvTotalHarga.text = formatRupiah.format(total)
    }
}
