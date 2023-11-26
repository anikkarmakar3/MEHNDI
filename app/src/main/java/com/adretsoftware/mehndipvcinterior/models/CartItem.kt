package com.adretsoftware.mehndipvcinterior.models

open class CartItem:Item() {
    var actual_price=""
    var user_id=""
    var total_price=""
    fun fromItem(item: Item){
        this.item_id=item.item_id
        this.quantity=item.quantity
        this.price=item.price
        this.code=item.code
        this.image_url=item.image_url
        this.name=item.name
        this.actual_price=item.price
    }

}