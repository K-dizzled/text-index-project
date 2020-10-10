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

        fun Trie<Char>.insert(string: String, wordIndex: Long) {
            insert(string.trim().toList(), wordIndex)
        }

        fun Trie<Char>.collections(prefix: String, wordIndex: Long): List<String> {
            return listForms(prefix.toList(), wordIndex).map { it.joinToString(separator = "") }
        }

        val trie = Trie<Char>()
        Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH)).use { reader ->
            CSVParser(reader, CSVFormat.DEFAULT).use { csvParser ->
                for (csvRecord in csvParser) {
                    // Accessing Values by Column Index
                    csvRecord.forEach {
                        trie.apply { insert(it, csvParser.recordNumber) }
                    }
                }
            }
        }

        println("\nWord forms of \"car\"")
        val prefixedWithCar = trie.collections("cat", 4)
        println(prefixedWithCar)
    }
}