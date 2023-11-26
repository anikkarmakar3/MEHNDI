package com.adretsoftware.mehndipvcinterior.daos

object Utilis {

    fun isLoginAsAdmin(): Boolean {
        if (MySharedStorage.getUserAdmin().equals("1", ignoreCase = true)) {
            return true
        }
        return false
    }

    fun getUserId(): String {
        return if (isLoginAsAdmin()) {
            ""
        } else {
            MySharedStorage.getUserId()
        }
    }
}