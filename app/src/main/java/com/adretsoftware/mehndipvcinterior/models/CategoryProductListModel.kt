package com.adretsoftware.mehndipvcinterior.models

import org.json.JSONObject

open class CategoryProductListModel (name: String = "", imageurl: String = "", cat_id: String = ""){
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
    var cat_id = ""

    init {
        this.name = name
        this.image_url = imageurl
        this.cat_id = cat_id
    }

    companion object {
        fun fromJsonObject(jsonObject: JSONObject): CategoryProductListModel {
            val catDetails = CategoryProductListModel()
//            item.item_id = jsonObject.getString("item_id")
            catDetails.name = jsonObject.getString("name")
//            item.code = jsonObject.getString("code")
            catDetails.image_url = jsonObject.getString("image")
            catDetails.cat_id = jsonObject.getString("cat_id")

//            item.parent = jsonObject.getString("parent")
//            item.price = jsonObject.getString("price")
//            item.quantity = jsonObject.getString("quantity")
//            item.about = jsonObject.getString("about")
//            item.features = jsonObject.getString("features")
//            item.status = jsonObject.getString("status")

            return catDetails
        }
    }
}