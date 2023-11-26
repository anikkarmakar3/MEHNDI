package com.adretsoftware.mehndipvcinterior.models

class InvoiceData {
    var user=User()
    var order=Order()
    var items= arrayListOf<OrderItem>()
}