package com.adretsoftware.mehndipvcinterior

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.adretsoftware.mehndipvcinterior.adapters.ViewPagerAdapter
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.databinding.ActivityUsersListBinding
import com.adretsoftware.mehndipvcinterior.dialogs.SpecificUsersList
import com.adretsoftware.mehndipvcinterior.fragments.OrdersFragment
import com.adretsoftware.mehndipvcinterior.fragments.OthersFragment
import com.adretsoftware.mehndipvcinterior.fragments.TransactionsFragment

class UsersList : AppCompatActivity() {
    lateinit var binding: ActivityUsersListBinding
    var userId: String = ""
    var userType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userType = intent.getStringExtra("userType").toString()
        userId = intent.getStringExtra("userId").toString()

        if (intent.getStringExtra("heading") != null) {
            binding.tvHeading.text = intent.getStringExtra("heading")
        }

        if (userType == Constants.MANUFACTURER) {
            binding.otherLayout.visibility = View.GONE
            binding.gridLayout.visibility = View.VISIBLE
        } else {
            binding.otherLayout.visibility = View.VISIBLE
            binding.gridLayout.visibility = View.GONE

            binding.tabLayout.setupWithViewPager(binding.viewPager)

            val ordersFragment = OrdersFragment(applicationContext, userId)
            val transactionsFragment = TransactionsFragment(this@UsersList, userId)
            val othersFragment = OthersFragment(userId, userType)

            val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
            viewPagerAdapter.addFragment(ordersFragment, "Orders")
            viewPagerAdapter.addFragment(transactionsFragment, "Transactions")

            if (userType != Constants.RETAILER) {
                viewPagerAdapter.addFragment(othersFragment, "Others")
            }

            binding.viewPager.adapter = viewPagerAdapter
        }

        binding.btnAgents.setOnClickListener {
            openDialog(Constants.AGENT, userId)
        }

        binding.btnDistributors.setOnClickListener {
            openDialog(Constants.DISTRIBUTER, userId)
        }

        binding.btnDealers.setOnClickListener {
            openDialog(Constants.DEALER, userId)
        }

        binding.btnRetailers.setOnClickListener {
            openDialog(Constants.RETAILER, userId)
        }
    }

    private fun openDialog(userType: String, userId: String) {
        val specificUsersList = SpecificUsersList(
            this@UsersList,
            R.style.Theme_MehndiInterior,
            userType,
            userId
        )
        specificUsersList.show()
    }
}