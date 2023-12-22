package com.adretsoftware.mehndipvcinterior.models

data class GetGalleryModel(
    val data: List<Data> = listOf(),
    val message: String = "",
    val status: Int = 0
)