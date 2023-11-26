package com.adretsoftware.mehndipvcinterior.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.databinding.FragmentOthersBinding
import com.adretsoftware.mehndipvcinterior.dialogs.SpecificUsersList

class OthersFragment(var userId: String, var userType: String) : Fragment() {

    lateinit var binding: FragmentOthersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOthersBinding.inflate(inflater, container, false)

        when (userType) {
            Constants.DISTRIBUTER -> {
                binding.gridLayout.removeView(binding.btnDistributors)
            }
            Constants.DEALER -> {
                binding.gridLayout.removeView(binding.btnDistributors)
                binding.gridLayout.removeView(binding.btnDealers)
            }
            Constants.RETAILER -> {
                binding.gridLayout.removeView(binding.btnDistributors)
                binding.gridLayout.removeView(binding.btnDealers)
                binding.gridLayout.removeView(binding.btnRetailers)
            }
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

        return binding.root
    }

    private fun openDialog(userType: String, userId: String) {
        context?.let {
            SpecificUsersList(
                it,
                R.style.Theme_MehndiInterior,
                userType,
                userId
            )
        }?.show()
    }
}