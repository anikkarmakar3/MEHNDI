package com.adretsoftware.mehndipvcinterior

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adretsoftware.mehndipvcinterior.adapters.ViewPagerAdapter
import com.adretsoftware.mehndipvcinterior.databinding.ActivityStatementsBinding
import com.adretsoftware.mehndipvcinterior.fragments.statementsfragments.StatementOrdersFragment
import com.adretsoftware.mehndipvcinterior.fragments.statementsfragments.StatementTransactionsFragment

class Statements : AppCompatActivity() {

    lateinit var binding: ActivityStatementsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tabLayout.setupWithViewPager(binding.viewPager)

        val ordersFragment = StatementOrdersFragment(this@Statements)
        val transactionsFragment = StatementTransactionsFragment(this@Statements)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ordersFragment, "Orders")
        viewPagerAdapter.addFragment(transactionsFragment, "Transactions")

        binding.viewPager.adapter = viewPagerAdapter
    }
}