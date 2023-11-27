package com.adretsoftware.mehndipvcinterior.daos

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.adretsoftware.mehndipvcinterior.models.User

@SuppressLint("StaticFieldLeak")
object MySharedStorage {
    lateinit var context: Context
    lateinit var preference: SharedPreferences
    lateinit var user: User
    lateinit var editor: SharedPreferences.Editor


    fun func(context: Context) {
        this.context = context
        preference = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        editor = preference.edit()
    }

    fun getUserId(): String {
        return preference.getString("user_id", "")!!
    }

    fun saveUser(user: User) {
        this.user = user
    }

    fun getUserr(): User {
        return user
    }

    fun getId() : String{
        return preference.getString("id", "")!!
    }

    fun setId(data: String){
        editor.putString("id", data)
        editor.apply()
    }
    fun setUserId(data: String) {
        editor.putString("user_id", data)
        editor.apply()
    }

    fun getUserType(): String {
        return preference.getString("user_type", "")!!
    }

    fun setUserType(data: String) {
        editor.putString("user_type", data)
        editor.apply()
    }

    fun setUserAdmin(data: String) {
        editor.putString("is_admin", data)
        editor.apply()
    }

    fun getUserAdmin(): String {
        return preference.getString("is_admin", "")!!
    }

//    fun getLoginId():String{
//        user_id = preference.getString("user_id",null)!!
//        return user_id
//    }
//    fun setLoginId(data:String){
//        editor.putString("user_id",data)
//        editor.apply()
//    }
//    fun getPassword():String{
//        password = preference.getString("password",null)!!
//        return password
//    }
//    fun setPassword(data:String){
//        editor.putString("password",data)
//        editor.apply()
//    }

    fun Logout() {
        editor.clear()
        editor.commit()

    }
}