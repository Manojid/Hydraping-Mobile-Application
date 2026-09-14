package com.hydroping.app.domain

import com.hydroping.app.data.DrinkLog
import com.hydroping.app.utils.CsvExportHelper
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvExportTest {

    @Test
    fun `test generateCsvString outputs headers and drink entries correctly`() {
        val drinks = listOf(
            DrinkLog(id = 1L, amountMl = 250, timestampMs = 1726300000000L, source = "QUICK_250"),
            DrinkLog(id = 2L, amountMl = 500, timestampMs = 1726303600000L, source = "APP")
        )

        val csv = CsvExportHelper.generateCsvString(drinks)

        assertTrue(csv.contains("ID,Date,Time,Amount_ml,Source"))
        assertTrue(csv.contains("1,"))
        assertTrue(csv.contains(",250,QUICK_250"))
        assertTrue(csv.contains("2,"))
        assertTrue(csv.contains(",500,APP"))
    }
}
