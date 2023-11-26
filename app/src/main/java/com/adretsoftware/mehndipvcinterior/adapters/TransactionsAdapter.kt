package com.adretsoftware.mehndipvcinterior.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.models.TransactionModel

class TransactionsAdapter(
    listener: TransactionFunctions,
    layoutInflater: LayoutInflater,
    val applicationContext: Context,
    command: String = "order"
) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    var listener: TransactionFunctions
    var items = arrayListOf<TransactionModel>()
    var command = ""

    init {
        this.listener = listener
        this.command = command
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.customview_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun update(items: ArrayList<TransactionModel>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (items[position].note.isEmpty()) {
            holder.tvOrderNote.visibility = View.GONE
        } else {
            holder.tvOrderNote.visibility = View.VISIBLE
        }

        holder.tvOrderNote.text = items[position].note
        holder.tvTransactionTime.text = items[position].date
        holder.tvTransactionAmount.text = items[position].amount

        holder.tvTransactionType.text = items[position].type
        if (items[position].type == "Received") {
            holder.tvTransactionType.setTextColor(applicationContext.getColor(R.color.green))
        } else {
            holder.tvTransactionType.setTextColor(applicationContext.getColor(R.color.red))
        }

        holder.layout.setOnLongClickListener {
            listener.itemClick(items[position])
            true
        }

        if (items[position].user_name.isNullOrEmpty()) {
            holder.usernameLayout.visibility = View.GONE
        } else {
            holder.usernameLayout.visibility = View.VISIBLE
            holder.tvUserName.text = items[position].user_name
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvOrderNote = view.findViewById<TextView>(R.id.tvOrderNote)
        var tvTransactionType = view.findViewById<TextView>(R.id.tvTransactionType)
        var tvTransactionTime = view.findViewById<TextView>(R.id.tvTransactionTime)
        var tvTransactionAmount = view.findViewById<TextView>(R.id.tvTransactionAmount)
        var layout = view.findViewById<LinearLayout>(R.id.layout)

        var usernameLayout = view.findViewById<RelativeLayout>(R.id.usernameLayout)
        var tvUserName = view.findViewById<TextView>(R.id.tvUserName)
    }
}

interface TransactionFunctions {
    fun itemClick(transactionModel: TransactionModel)
}