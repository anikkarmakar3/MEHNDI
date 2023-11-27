package com.adretsoftware.mehndipvcinterior.adapters

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.models.InvoiceModel

class InvoiceItemAdapter(private val context:Context): RecyclerView.Adapter<InvoiceItemAdapter.ViewHolder>() {

    private var invoiceData = ArrayList<InvoiceModel>()

    fun updateDataToAdapter(data:ArrayList<InvoiceModel>){
        this.invoiceData = data
        notifyDataSetChanged()
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val name: TextView
        internal val image: ImageView
        internal val button: Button
        init {
            name = itemView.findViewById(R.id.subj_name)
            image = itemView.findViewById(R.id.pdf_image)
            button = itemView.findViewById(R.id.download_btn)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.invoice_item_layout, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return invoiceData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resultString = invoiceData[position].url.substringAfterLast('/')
        holder.name.text = resultString
        holder.button.setOnClickListener{
            try {
                var download= context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                var PdfUri = Uri.parse("https://"+invoiceData[position].url)
                var getPdf = DownloadManager.Request(PdfUri)
                getPdf.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                download.enqueue(getPdf)
                Toast.makeText(context,"Download Started", Toast.LENGTH_LONG).show()
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}