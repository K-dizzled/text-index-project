package ru.emkn.kotlin

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

object BasicCSVReader {
    private const val SAMPLE_CSV_FILE_PATH = "./data/testData.csv"
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH)).use { reader ->
            CSVParser(reader, CSVFormat.DEFAULT).use { csvParser ->
                for (csvRecord in csvParser) {
                    // Accessing Values by Column Index
                    println(csvRecord.forEach { it.toList() })
                }
            }
        }
    }
}