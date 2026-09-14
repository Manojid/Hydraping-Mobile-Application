package com.hydroping.app.utils

import android.content.Context
import android.net.Uri
import com.hydroping.app.data.DrinkLog
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExportHelper {

    fun generateCsvString(drinks: List<DrinkLog>): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        val builder = StringBuilder()
        builder.append("ID,Date,Time,Amount_ml,Source\n")

        drinks.forEach { drink ->
            val dateStr = dateFormat.format(Date(drink.timestampMs))
            val timeStr = timeFormat.format(Date(drink.timestampMs))
            builder.append("${drink.id},$dateStr,$timeStr,${drink.amountMl},${drink.source}\n")
        }

        return builder.toString()
    }

    fun writeCsvToUri(context: Context, uri: Uri, csvContent: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(csvContent)
                    writer.flush()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
