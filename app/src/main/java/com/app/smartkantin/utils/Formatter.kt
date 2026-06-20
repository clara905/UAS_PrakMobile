package com.app.smartkantin.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object Formatter {
    fun rupiah(value: Double): String {
        val symbols = DecimalFormatSymbols(Locale("in", "ID")).apply {
            groupingSeparator = '.'
        }
        val df = DecimalFormat("#,###", symbols)
        return "Rp ${df.format(value)}"
    }
}