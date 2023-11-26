package com.adretsoftware.mehndipvcinterior.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.models.InvoicesModel
import com.bumptech.glide.Glide


class InvoicesItemAdapter(
    listener: InvoiceItemClickListener,
    layoutInflater: LayoutInflater,
    applicationContext: Context
) : RecyclerView.Adapter<InvoicesItemAdapter.ViewHolder>() {

    var listener: InvoiceItemClickListener
    var items = arrayListOf<InvoicesModel>()

    init {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.inflator_invoice_picture, parent, false)
        val vh = ViewHolder(view)
        return vh
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun update(items: ArrayList<InvoicesModel>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = Constants.pdfUrl + items[position].invo_lbl + ".pdf"
        holder.layout.setOnClickListener {
            listener.onItemClick(url, items[position].invo_lbl + ".pdf")
        }

//        if (items[position].invo_lbl.contains(".pdf")) {
            Glide.with(holder.itemView.context)
                .load(holder.itemView.context.getDrawable(R.drawable.pdf_icon))
                .into(holder.image)
//        } else {
//            Glide.with(holder.itemView.context).load(url).into(holder.image)
//        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var layout = view.findViewById<LinearLayout>(R.id.layout)
        var image = view.findViewById<ImageView>(R.id.item_image)
    }
}

interface InvoiceItemClickListener {
    fun onItemClick(url: String, name: String)
}
