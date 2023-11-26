package com.adretsoftware.mehndipvcinterior.models

class ManageProductModel(
    user_id: String = "",
    parent_id: String = "",
    item_id: String = "",
    price: String = "",
    status: String = ""
) {
    var user_id = ""
    var parent_id = ""
    var item_id = ""
    var price = ""
    var status = ""

    init {
        this.user_id = user_id
        this.parent_id = parent_id
        this.item_id = item_id
        this.price = price
        this.status = status
    }
}