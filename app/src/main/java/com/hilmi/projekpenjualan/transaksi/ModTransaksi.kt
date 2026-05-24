package com.hilmi.projekpenjualan.transaksi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.model.CartItem
import java.text.NumberFormat
import java.util.Locale

class ModTransaksi : AppCompatActivity() {

    private lateinit var llItemSummary: LinearLayout
    private lateinit var tvTotalHarga: TextView
    private lateinit var rbQrisButton: RadioButton
    private lateinit var rbTunaiButton: RadioButton
    private lateinit var btnPesan: CardView
    private var totalPrice: Int = 0

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
        btnPesan = findViewById(R.id.btnPesan)

        val selectedItems = intent.getParcelableArrayListExtra<CartItem>("SELECTED_ITEMS")
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
            when {
                rbQrisButton.isChecked -> showQrisPopup()
                rbTunaiButton.isChecked -> {
                    Toast.makeText(this, "Pembayaran Tunai Dipilih", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Silakan pilih metode pembayaran", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showQrisPopup() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_popup_qris, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Make background transparent so card corners are visible
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvTotalHargaQris = dialogView.findViewById<TextView>(R.id.tvTotalHargaQris)
        val btnLanjutkan = dialogView.findViewById<MaterialButton>(R.id.btnLanjutkan)

        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        tvTotalHargaQris.text = formatRupiah.format(totalPrice).replace("Rp", "").trim()

        btnLanjutkan.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Pembayaran Berhasil", Toast.LENGTH_SHORT).show()
            finish()
        }

        dialog.show()
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
