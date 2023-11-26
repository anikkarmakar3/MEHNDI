package com.adretsoftware.mehndipvcinterior.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.daos.Utilis
import com.adretsoftware.mehndipvcinterior.models.User

class UserAdapter(listener: userFunctions, val whichScreen: String) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    var listener: userFunctions
    var users = (arrayListOf<User>())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.customview_user, parent, false)
        val vh = UserAdapter.ViewHolder(view)
        return vh
    }

    init {
        this.listener = listener
    }

    fun update(users: ArrayList<User>) {
        this.users = users
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.email.text = users[position].email
        holder.name.text = users[position].name
        holder.number.text = users[position].mobile
        holder.root.setOnClickListener(View.OnClickListener {
            listener.itemClick(users[position])
        })

        if (users[position].total_orders_price.isNullOrEmpty()) {
            holder.layoutProfit.visibility = View.GONE
        } else {
            holder.layoutProfit.visibility = View.VISIBLE
            try {
                val price =
                    users[position].total_orders_price.toDouble() - users[position].total_items_price.toDouble()
                holder.tvProfit.text = price.toString()

                if (price.toString().contains("-")) {
                    holder.tvProfit.setTextColor(holder.tvProfit.context.getColor(R.color.red))
                } else {
                    holder.tvProfit.setTextColor(holder.tvProfit.context.getColor(R.color.green))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (Utilis.isLoginAsAdmin() && !users[position].status.isNullOrEmpty() && whichScreen == "users") {
            holder.btnBlockUsers.visibility = View.VISIBLE
            if (users[position].status == "Active") {
                holder.btnBlockUsers.text = "Block User"
                holder.btnBlockUsers.backgroundTintList =
                    ColorStateList.valueOf(holder.btnBlockUsers.context.getColor(R.color.red))
            } else {
                holder.btnBlockUsers.text = "Unblock User"
                holder.btnBlockUsers.backgroundTintList =
                    ColorStateList.valueOf(holder.btnBlockUsers.context.getColor(R.color.green))
            }

            holder.btnBlockUsers.setOnClickListener {
                listener.blockUser(users[position])
            }
        } else {
            holder.btnBlockUsers.visibility = View.GONE
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name = view.findViewById<TextView>(R.id.user_name)
        var email = view.findViewById<TextView>(R.id.user_email)
        var number = view.findViewById<TextView>(R.id.user_number)
        var root = view.findViewById<CardView>(R.id.user_root)
        var layoutProfit = view.findViewById<LinearLayout>(R.id.layoutProfit)
        var tvProfit = view.findViewById<TextView>(R.id.tvProfit)
        var btnBlockUsers = view.findViewById<Button>(R.id.btnBlockUsers)
    }
}

interface userFunctions {
    fun itemClick(user: User)
    fun blockUser(user: User)
}