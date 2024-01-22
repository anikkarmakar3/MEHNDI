package com.adretsoftware.mehndipvcinterior.adapters

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.models.MyChartModelItem
import com.adretsoftware.mehndipvcinterior.models.MyTeamAdapterItem

class MyChartAdapter(val context : Context): RecyclerView.Adapter<MyChartAdapter.ChartViewHolder>() {
    var chartList = ArrayList<MyChartModelItem>()
    inner class ChartViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val name = view.findViewById<TextView>(R.id.namedata)
    }

    fun updateTeamMeambers(newCharList:ArrayList<MyChartModelItem>){
        this.chartList = newCharList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chrt_item, parent, false)
        return ChartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chartList.size
    }

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        holder.name.text = chartList[position].name
        holder.itemView.setOnClickListener{
            try {
                var download= context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                var PdfUri = Uri.parse(Constants.apiUrl2+chartList[position].file.substringAfterLast("../"))
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