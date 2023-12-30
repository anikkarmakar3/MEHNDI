package com.adretsoftware.mehndipvcinterior.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.models.MyTeamAdapterItem

class MyTeamAdapter(val context : Context): RecyclerView.Adapter<MyTeamAdapter.TeamViewHolder>() {

    var teamList = ArrayList<MyTeamAdapterItem>()
    inner class TeamViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val name = view.findViewById<TextView>(R.id.namedata)
        val userType = view.findViewById<TextView>(R.id.user_type_data)
    }

    fun updateTeamMeambers(newTeamMembers:ArrayList<MyTeamAdapterItem>){
        this.teamList = newTeamMembers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.team_item, parent, false)
        return TeamViewHolder(view)
    }

    override fun getItemCount(): Int {
        return teamList.size
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.name.text = teamList[position].name
        holder.userType.text = teamList[position].user_type
    }
}