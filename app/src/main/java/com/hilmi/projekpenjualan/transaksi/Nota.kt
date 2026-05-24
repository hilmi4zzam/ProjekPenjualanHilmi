package com.hilmi.projekpenjualan.transaksi

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.print.PrintHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.model.CartItem
import java.text.NumberFormat
import java.util.Locale

class Nota : AppCompatActivity() {

    private lateinit var llItemSummary: LinearLayout
    private lateinit var tvTotalHarga: TextView
    private lateinit var cvNota: MaterialCardView
    private lateinit var btnCetak: MaterialButton
    private lateinit var btnSelesai: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nota)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        llItemSummary = findViewById(R.id.llItemSummary)
        tvTotalHarga = findViewById(R.id.tvTotalHarga)
        cvNota = findViewById(R.id.cvNota)
        btnCetak = findViewById(R.id.btnCetak)
        btnSelesai = findViewById(R.id.btnSelesai)

        val selectedItems = intent.getParcelableArrayListExtra<CartItem>("SELECTED_ITEMS")
        val totalPrice = intent.getIntExtra("TOTAL_PRICE", 0)

        displayItems(selectedItems)
        displayTotalPrice(totalPrice)

        btnSelesai.setOnClickListener {
            val intent = Intent(this, DataTransaksi::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        btnCetak.setOnClickListener {
            cetakNota()
        }
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

    private fun cetakNota() {
        val bitmap = createBitmapFromView(cvNota)
        val printHelper = PrintHelper(this)
        printHelper.scaleMode = PrintHelper.SCALE_MODE_FIT
        printHelper.printBitmap("Nota Belanja", bitmap)
    }

    private fun createBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}
