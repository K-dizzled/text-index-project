package ru.emkn.kotlin

import org.apache.commons.csv.*
import java.nio.charset.Charset
import java.nio.file.*

fun parseCSV(): Trie<Char> {

    val csvFilePath = "odict.csv"

    fun Trie<Char>.insert(string: String, wordIndex: Long) {
        insert(string.trim().toList(), wordIndex)
    }

    val dictionary = Trie<Char>()
    // Function gets the csv file, skips the first line, converts it
    // from cp1251 encoding and uses every line to build a Trie
    Files.newBufferedReader(Paths.get(csvFilePath), Charset.forName("Windows-1251")).use { reader ->
        CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader()).use { csvParser ->
            for (csvRecord in csvParser) {
                // Inserting Values with it's row number
                // to use it later as a word index
                csvRecord.forEach {
                    dictionary.apply { insert(it, csvParser.recordNumber) }
                }
            }
        }
    }

    return dictionary
}