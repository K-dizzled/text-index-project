package ru.emkn.kotlin

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

fun parseCSV(): Trie<Char> {

    val csvFilePath = "odict.csv"

    fun Trie<Char>.insert(string: String, wordIndex: Long) {
        insert(string.trim().toList(), wordIndex)
    }

    val dictionary = Trie<Char>()
    Files.newBufferedReader(Paths.get(csvFilePath), Charset.forName("Windows-1251")).use { reader ->
        CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader()).use { csvParser ->
            for (csvRecord in csvParser) {
                // Accessing Values by Column Index
                csvRecord.forEach {
                    dictionary.apply { insert(it, csvParser.recordNumber) }
                }
            }
        }
    }

    return dictionary
}