package com.adretsoftware.mehndipvcinterior.models

import com.google.gson.JsonObject
import org.json.JSONObject

open class Item(name: String = "", imageurl: String = "", price: String = "") {
    var item_id = ""
    var name = ""
    var code = ""
    var image_url = ""
    var parent = ""
    var price = ""
    var quantity = ""
    var about = ""
    var features = ""
    var discount = ""
    var amount = ""
    var individual_discount = ""
    var user_set_price = ""
    var user_set_status = ""
    var status = ""
    var id = ""

    init {
        this.name = name
        this.image_url = imageurl
        this.price = price
    }

    companion object {
        fun fromJsonObject(jsonObject: JSONObject): Item {
            val item = Item()
//            item.item_id = jsonObject.getString("item_id")
            item.name = jsonObject.getString("name")
//            item.code = jsonObject.getString("code")
            item.image_url = jsonObject.getString("image")
            item.id = jsonObject.getString("id")

//            item.parent = jsonObject.getString("parent")
//            item.price = jsonObject.getString("price")
//            item.quantity = jsonObject.getString("quantity")
//            item.about = jsonObject.getString("about")
//            item.features = jsonObject.getString("features")
//            item.status = jsonObject.getString("status")

            return item
        }
    }
}