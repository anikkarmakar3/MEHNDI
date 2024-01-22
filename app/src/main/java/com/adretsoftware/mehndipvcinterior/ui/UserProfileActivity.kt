package com.adretsoftware.mehndipvcinterior.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.adretsoftware.mehndipvcinterior.*
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage.user
import com.adretsoftware.mehndipvcinterior.daos.Utilis
import com.adretsoftware.mehndipvcinterior.databinding.ActivityUserProfileBinding
import com.adretsoftware.mehndipvcinterior.databinding.LogoutBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile)
        binding.accountId.text = MySharedStorage.getUserId()
        init()
    }

    private fun init() {

        onclickListener()
    }

    private fun onclickListener() {
        if (Utilis.isLoginAsAdmin()) {
            binding.lnrUserListProfile.visibility = View.VISIBLE
        } else {
            binding.lnrUserListProfile.visibility = View.GONE

        }
        binding.lnrUserListProfile.setOnClickListener {
            Intent(this, Users::class.java).apply {

                startActivity(this)
            }
        }

        binding.lnrEditProfile.setOnClickListener {
            val gson = Gson()
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("user", gson.toJson(user))
            startActivity(intent)
//            Intent(this, EditProfileActivity::class.java).apply {
//                startActivity(this)
//            }
        }

        binding.lnrChangePasswordProfile.setOnClickListener {
            Intent(this, ChangePasswordActivity::class.java).apply {
                startActivity(this)
            }
        }

        binding.lnrLogoutProfile.setOnClickListener {
            bottomsheetDialog()
        }
    }

    private fun bottomsheetDialog() {
        val bottomSheet = BottomSheetDialog(this)
        val bindingSheet = DataBindingUtil.inflate<LogoutBottomSheetBinding>(
            layoutInflater,
            R.layout.logout_bottom_sheet,
            null,
            false
        )

        bindingSheet.txtCancelLogout.setOnClickListener {
            bottomSheet.dismiss()
        }


        bindingSheet.txtLogout.setOnClickListener {
            bottomSheet.dismiss()
            MySharedStorage.Logout()

            GotoLoginActivity()

        }
        bottomSheet.setContentView(bindingSheet.root)
        bottomSheet.show()
    }


    private fun GotoLoginActivity() {
        Intent(this@UserProfileActivity, Login::class.java).apply {

            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)

        }
    }
}