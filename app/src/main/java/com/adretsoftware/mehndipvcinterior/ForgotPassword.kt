package com.adretsoftware.mehndipvcinterior

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityForgotPasswordBinding
import com.adretsoftware.mehndipvcinterior.models.RetrofitResponse
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class ForgotPassword : AppCompatActivity() {

    lateinit var binding: ActivityForgotPasswordBinding
    var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    var mVerificationId: String? = null
    var mResendToken: PhoneAuthProvider.ForceResendingToken? = null

    private lateinit var progressDialog: ProgressDialog
    private fun showLoading() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.show()
    }

    private fun closeLoading() {
        progressDialog.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mobileEnterScreen()
        binding.btnGetOtp.setOnClickListener {
            if (binding.etMobileNumber.text.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Enter your registered phone number",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showLoading()
                val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                    .setPhoneNumber("+91" + binding.etMobileNumber.text)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(mCallbacks!!)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }

        binding.btnVerifyOtp.setOnClickListener {
            if (binding.etOtpEnter.text.length == 6) {
                showLoading()
                val credential = PhoneAuthProvider.getCredential(
                    mVerificationId!!, binding.etOtpEnter.text.toString()
                )
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(
                    applicationContext,
                    "Enter 6 digits otp",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationFailed(e: FirebaseException) {
                closeLoading()
                Toast.makeText(
                    this@ForgotPassword,
                    "Something went wrong! " + e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                mVerificationId = verificationId
                mResendToken = token
                otpEnterScreen()
                closeLoading()
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(p0)
            }
        }

        binding.btnSavePassword.setOnClickListener {
            if (binding.etNewPassword.text.toString().isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Enter your new password",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showLoading()
                val phone = RequestBody.create(
                    MediaType.parse("text/plain"),
                    binding.etMobileNumber.text.toString()
                )
                val password = RequestBody.create(
                    MediaType.parse("text/plain"),
                    binding.etNewPassword.text.toString()
                )

                RetrofitClient.getApiHolder().resetUserPassword(phone, password)
                    .enqueue(object : Callback<RetrofitResponse> {
                        override fun onResponse(
                            call: Call<RetrofitResponse>,
                            response: Response<RetrofitResponse>
                        ) {
                            closeLoading()
                            if (response.code() == Constants.code_OK) {
                                Toast.makeText(
                                    this@ForgotPassword,
                                    "Password changed successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                Toast.makeText(
                                    this@ForgotPassword,
                                    response.body()?.message + "",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {

                        }
                    })
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                closeLoading()
                if (task.isSuccessful) {
                    passwordEnterScreen()
                } else {
                    Toast.makeText(
                        this@ForgotPassword,
                        "Something went wrong! " + task.exception,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun mobileEnterScreen() {
        binding.mobileEnterLayout.visibility = View.VISIBLE
        binding.otpEnterLayout.visibility = View.GONE
        binding.passwordEnterLayout.visibility = View.GONE
    }

    private fun otpEnterScreen() {
        binding.mobileEnterLayout.visibility = View.GONE
        binding.otpEnterLayout.visibility = View.VISIBLE
        binding.passwordEnterLayout.visibility = View.GONE
    }

    private fun passwordEnterScreen() {
        binding.mobileEnterLayout.visibility = View.GONE
        binding.otpEnterLayout.visibility = View.GONE
        binding.passwordEnterLayout.visibility = View.VISIBLE
    }
}