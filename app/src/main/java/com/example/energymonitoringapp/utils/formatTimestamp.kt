package com.example.energymonitoringapp.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun formatTimestamp(timestamp: String?): String {
    if (timestamp.isNullOrBlank()) return "-"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val outputFormat = SimpleDateFormat("d MMMM yyyy, HH.mm", Locale("id", "ID"))

        val date = inputFormat.parse(timestamp)
        "${outputFormat.format(date!!)} WIB"
    } catch (e: Exception) {
        timestamp
    }
}