package ru.emkn.kotlin

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


fun main(args: Array<String>) {
    analyzeText("data/Childhood.txt")

    val json = String(Files.readAllBytes(Paths.get("data/testTextIndex.json")))
    val document: Any = Configuration.defaultConfiguration().jsonProvider().parse(json)
    val books
            = JsonPath.read<List<Map<String, Any>>>(
        document,
        "$[?(@.amount == 1)]"
    )
    println("$books ")
}

fun analyzeText(file: String) {
    fun Trie<Char>.findIndex(string: String): Long {
        return findIndex(string.toList())
    }

    //Parse dictionary
    val dict = parseCSV()
    //Create a Set of Words where we will store all primary data
    val wordIndex : HashMap<Long, Word> = LinkedHashMap()

    var pageIndex = 1
    var lineIndex = 1
    val regx = Regex("[^А-Яа-яA-Za-z0-9]")
    var index : Long
    File(file).forEachLine { line ->
        if (line.trim() != "") {
            val wordList: List<String> = line.trim().split(" ")
            wordList.forEach {
                index = dict.findIndex((regx.replace(it, "").toLowerCase()))
                if(index.toInt() != -1) {
                    if (!wordIndex.containsKey(index)){
                        wordIndex[index] = Word(index)
                    }
                    wordIndex[index]?.addForm(regx.replace(it, "").toLowerCase())
                    wordIndex[index]?.addPage(pageIndex)
                    wordIndex[index]?.addLine(lineIndex)
                    wordIndex[index]?.increaseAmount()
                }
            }
            lineIndex++
            if(lineIndex == 45) {
                lineIndex = 1
                pageIndex++
            }
        }
    }
    val jsonString = Gson().toJson(wordIndex.values)
    val gson = GsonBuilder().setPrettyPrinting().create()
    val je: JsonElement = JsonParser().parse(jsonString)
    val prettyJsonString = gson.toJson(je)
    File("data/testTextIndex.json").writeText(prettyJsonString)
}

class Word(index: Long) {
    @SerializedName("category") val category = ""
    @SerializedName("id") val id = index
    @SerializedName("forms") var usedForms = mutableListOf<String>()
    @SerializedName("pageIndex") var pageIndex = mutableListOf<Int>()
    @SerializedName("lineIndex") var lineIndex = mutableListOf<Int>()
    @SerializedName("amount") var amount = 0

    fun increaseAmount() { this.amount++ }

    fun addForm(form: String) {
        if (!usedForms.contains(form))
            usedForms.add(form)
    }

    fun addPage(page: Int) {
        if (!pageIndex.contains(page))
            pageIndex.add(page)
    }

    fun addLine(line: Int) {
        if (!lineIndex.contains(line))
            lineIndex.add(line)
    }
}
