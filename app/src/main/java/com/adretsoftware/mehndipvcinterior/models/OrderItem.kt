package com.adretsoftware.mehndipvcinterior.models

class OrderItem: CartItem() {
    var order_id=""

    fun fromCartItem(cartItem: CartItem){
        this.item_id=cartItem.item_id
        this.quantity=cartItem.quantity
        this.price=cartItem.price
        this.user_id=cartItem.user_id
        this.code=cartItem.code
        this.image_url=cartItem.image_url
        this.name=cartItem.name
        this.actual_price=cartItem.actual_price
        this.total_price=cartItem.total_price

    }

}