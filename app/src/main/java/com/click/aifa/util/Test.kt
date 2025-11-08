package com.click.aifa.util

import com.click.aifa.R
import com.click.aifa.ui.addTransaction.adapter.Transaction

object Test {
     fun getSampleData(): List<Transaction> {
    return    listOf(
            Transaction("Sallary", "30 Apr 2022", "+$1500", R.drawable.ic_transaction, true),
            Transaction("Paypal", "28 Apr 2022", "+$3500", R.drawable.ic_transaction, true),
            Transaction("Food", "25 Apr 2022", "-$300", R.drawable.ic_transaction, false),
            Transaction("Upwork", "23 Apr 2022", "+$800", R.drawable.ic_transaction, true),
            Transaction("Bill", "22 Apr 2022", "-$600", R.drawable.ic_transaction, false),
            Transaction("Discount", "20 Apr 2022", "+$200", R.drawable.ic_transaction, true)
        )
    }
}