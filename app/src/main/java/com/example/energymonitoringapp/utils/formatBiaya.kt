package com.example.energymonitoringapp.utils

import java.text.NumberFormat
import java.util.Locale

fun formatBiaya(value: Double?): String {
    if (value == null) return "-"
    val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
    format.minimumFractionDigits = 2
    format.maximumFractionDigits = 2
    return "Rp ${format.format(value)}"
}