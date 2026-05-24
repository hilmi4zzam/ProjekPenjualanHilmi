package com.hilmi.projekpenjualan.transaksi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.LayoutInflater
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.hilmi.projekpenjualan.R
import com.hilmi.projekpenjualan.model.CartItem
import com.hilmi.projekpenjualan.model.ModelProduk
import java.text.NumberFormat
import java.util.Locale

class Nota : AppCompatActivity() {

    private lateinit var llItemSummary: LinearLayout
    private lateinit var tvTotalHarga: TextView
    private lateinit var tvNamaPemesan: TextView
    private lateinit var btnCetak: MaterialButton
    private lateinit var btnBagikan: MaterialButton
    private lateinit var btnSelesai: MaterialButton
    private var isStockUpdated = false

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
        tvNamaPemesan = findViewById(R.id.tvNama)
        btnCetak = findViewById(R.id.btnCetak)
        btnBagikan = findViewById(R.id.btnBagikan)
        btnSelesai = findViewById(R.id.btnSelesai)

        val selectedItems = intent.getParcelableArrayListExtra<CartItem>("SELECTED_ITEMS")
        val totalPrice = intent.getIntExtra("TOTAL_PRICE", 0)
        val namaPemesan = intent.getStringExtra("NAMA_PEMESAN")

        displayItems(selectedItems)
        displayTotalPrice(totalPrice)
        tvNamaPemesan.text = namaPemesan

        btnSelesai.setOnClickListener {
            updateStockInFirebase(selectedItems)
            val intent = Intent(this, DataTransaksi::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        btnCetak.setOnClickListener {
            updateStockInFirebase(selectedItems)
            cetakNota()
        }

        btnBagikan.setOnClickListener {
            showShareDialog()
        }
    }

    private fun generateReceiptText(): String {
        val selectedItems = intent.getParcelableArrayListExtra<CartItem>("SELECTED_ITEMS")
        val totalPrice = intent.getIntExtra("TOTAL_PRICE", 0)
        val namaPemesan = intent.getStringExtra("NAMA_PEMESAN")

        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        
        val sb = StringBuilder()
        sb.append("==============================\n")
        sb.append("        HILMI STORE        \n")
        sb.append("==============================\n")
        sb.append("Nama: ${namaPemesan ?: "-"}\n")
        sb.append("------------------------------\n")
        
        selectedItems?.forEach { item ->
            val pricePerItem = item.hargaProduk ?: 0
            val totalItemPrice = pricePerItem * (item.jumlah ?: 0)
            sb.append("${item.namaProduk}\n")
            sb.append("${item.jumlah} x ${formatRupiah.format(pricePerItem)} = ${formatRupiah.format(totalItemPrice)}\n")
        }
        
        sb.append("------------------------------\n")
        sb.append("TOTAL: ${formatRupiah.format(totalPrice)}\n")
        sb.append("==============================\n")
        sb.append("Terima kasih atas kunjungannya!\n")
        sb.append("==============================\n")
        
        return sb.toString()
    }

    private fun showShareDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_input_nomor, null)
        val etNomor = dialogView.findViewById<TextInputEditText>(R.id.etNomorWA)

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Bagikan ke WhatsApp")
        builder.setView(dialogView)
        builder.setPositiveButton("Kirim") { dialog, _ ->
            val nomor = etNomor.text.toString().trim()
            if (nomor.isNotEmpty()) {
                shareToWhatsApp(nomor)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Nomor tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun shareToWhatsApp(nomor: String) {
        var formattedNomor = nomor
        if (formattedNomor.startsWith("0")) {
            formattedNomor = "62" + formattedNomor.substring(1)
        } else if (!formattedNomor.startsWith("62")) {
            formattedNomor = "62" + formattedNomor
        }

        val receiptText = generateReceiptText()
        
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, receiptText)
        intent.putExtra("jid", "$formattedNomor@s.whatsapp.net")
        intent.`package` = "com.whatsapp"

        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback for direct URL if needed
            try {
                val url = "https://api.whatsapp.com/send?phone=$formattedNomor&text=${java.net.URLEncoder.encode(receiptText, "UTF-8")}"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = android.net.Uri.parse(url)
                startActivity(i)
            } catch (ex: Exception) {
                Toast.makeText(this, "WhatsApp tidak terinstal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cetakNota() {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "${getString(R.string.app_name)} Document"
        
        val receiptText = generateReceiptText().replace("\n", "<br>")
        val htmlContent = "<html><body><pre style='font-family: monospace; font-size: 10pt;'>$receiptText</pre></body></html>"
        
        val webView = WebView(this)
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter(jobName)
                printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
            }
        }
    }

    private fun updateStockInFirebase(items: ArrayList<CartItem>?) {
        if (isStockUpdated || items.isNullOrEmpty()) return

        val database = FirebaseDatabase.getInstance()
        val productsRef = database.getReference("produk")

        for (item in items) {
            val idProduk = item.idProduk ?: continue
            val quantityToSubtract = item.jumlah ?: 0

            productsRef.child(idProduk).runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val product = currentData.getValue(ModelProduk::class.java) ?: return Transaction.success(currentData)
                    val currentStock = product.stokProduk ?: 0
                    val newStock = (currentStock - quantityToSubtract).coerceAtLeast(0)
                    currentData.child("stokProduk").value = newStock
                    if (newStock == 0) {
                        currentData.child("statusProduk").value = "Nonaktif"
                    }
                    return Transaction.success(currentData)
                }

                override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                    if (error != null) {
                        runOnUiThread {
                            Toast.makeText(this@Nota, "Gagal memperbarui stok: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
        isStockUpdated = true
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
