package com.adretsoftware.mehndipvcinterior.models

class TransactionModel(
    user_id: String,
    for_user: String,
    type: String,
    amount: String,
    note: String,
) {
    var user_id = ""
    var for_user = ""
    var type = ""
    var amount = ""
    var note = ""
    var date = ""
    var user_name = ""

    init {
        this.user_id = user_id
        this.for_user = for_user
        this.type = type
        this.amount = amount
        this.note = note
    }
}