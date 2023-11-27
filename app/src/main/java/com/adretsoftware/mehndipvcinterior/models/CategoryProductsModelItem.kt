package com.adretsoftware.mehndipvcinterior.models

data class CategoryProductsModelItem(
    val about: String = "",
    val cat_id: String = "",
    val code: String = "",
    val features: String = "",
    val id: String = "",
    val image: List<String> = listOf(),
    val name: String = "",
    val price: String = "",
    val quantity: String = "",
    val status: String = ""
)