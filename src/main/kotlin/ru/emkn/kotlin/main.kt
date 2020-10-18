package ru.emkn.kotlin

//CliKt
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
//JSON
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
//Files
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


fun main(args: Array<String>) = Interface().main(args)

fun printMostUsedWords(top: List<Map<String, Any>>, topUsedWordsListSize: Int) {
    var topList = top
    //Print 5 most used words from the text
    print("Топ $topUsedWordsListSize самых часто встречающихся в тексте слов: ")
    topList = topList.sortedByDescending { it["amount"].toString().toInt() }
    var number = 0
    var ind = 0
    while (number < topUsedWordsListSize) {
        // As sometimes in odict the second word indicates form of the word, we check
        // its length to be more than 4
        if(topList[ind]["forms"].toString().substringBefore("\",\"")
                        .substringBefore("\"]").length > 6) {
            if (number == topUsedWordsListSize - 1) {
                print(topList[ind]["forms"].toString()
                        .substringAfter("[\"").substringBefore("\""))
                println()
            }
            else{
                print("${topList[ind]["forms"].toString()
                        .substringAfter("[\"").substringBefore("\"")}, ")
            }
            number++
        }
        ind++
    }
}

class Interface : CliktCommand() {
    private val task: Int by option(help = "Task number").int().default(1)
    private val word: String by option(help = "Word to look for").default("ужин")
    private val category: String by option(help = "Category to look for").default("еда")
    private val input: String by option(help = "Path for input text").default("data/Childhood.txt")
    private val top: Int by option(help = "Define amount of most used words needed").int().default(5)

    override fun run() {
        // Here we check, if the given file exists
        val path = "data/${input.substringAfterLast("/").substringBefore(".")}TextIndex.json"
        if (File(input).exists()) {
            // If input path is correct, we check if we already have a built
            // index for that file, and if not - build it
            if (!File(path).exists())
                analyzeText(input)
            val json = String(Files.readAllBytes(Paths.get(path)))
            val document: Any = Configuration.defaultConfiguration().jsonProvider().parse(json)
            if (word.all { it.isLetter() }) {
                when(task) {
                    1 -> task1(word, document)
                    2 -> task2(word, category, document, top)
                    3 -> task3(word, document, input)
                    else -> println("Некорректный номер задания")
                }
            }
            else
                println("Error: Слово указано неверно, проверьте входные данные")
        }
        else {
            println("Error: Имя файла для анализа указано неверно, проверьте входные данные")
        }
    }

}

fun task1(wordToBeFound: String, document: Any) : List<String> {
    val pages
            = JsonPath.read<List<Map<String, Any>>>(
            document,
            "$[?('$wordToBeFound'in @.forms)]['pageIndex']"
    )
    val answer = pages.toString().substringAfterLast("[")
            .substringBefore("]").split(",")
    println("Страницы, на которых найдены формы слова \"$wordToBeFound\": ${answer.joinToString()}")
    return answer
}

fun task2(wordToBeFound: String, category: String, document: Any, topUsedWordsListSize: Int) : Long {
    val info
            = JsonPath.read<List<Map<String, Any>>>(
            document,
            "$[?('$wordToBeFound'in @.forms)]"
    )
    val fromCategory = JsonPath.read<List<Map<String, Any>>>(
            document,
            "$[?(@.category == '$category')]['forms']"
    )
    val topList = JsonPath.read<List<Map<String, Any>>>(
            document,
            "$..['amount','forms']"
    )

    println("Страницы, на которых найдены формы слова \"$wordToBeFound\": " +
            info.toString().substringAfter("pageIndex\":[")
                    .substringBefore("],").split(",").joinToString())
    // Forms of the word, that were used in the text
    println("Формы, в которых встречается слово \"$wordToBeFound\": " +
            info.toString().substringAfter("forms\":[\"")
                    .substringBefore("\"]").split("\",\"").joinToString())
    // Amount of times the word or it's forms were used
    val amount = info.toString().substringAfter("amount\":").substringBefore("}")
    println("Количество раз, когда встретилось слово \"$wordToBeFound\": $amount")
    // Print words from given category
    println("Формы, в которых встречаются слова из категории \"$category\": " +
            fromCategory.toString().replace("\",\"", ", ")
                    .replace("\"],[\"", ", ").substringAfter("[[\"")
                    .substringBefore("\"]]"))

    printMostUsedWords(topList, topUsedWordsListSize)
    return if((amount == "")||(amount == "[]")) 0 else amount.toLong()
}

fun task3(wordToBeFound: String, document: Any, path: String) {
    val linesJ
            = JsonPath.read<List<Map<String, Any>>>(
            document,
            "$[?('$wordToBeFound'in @.forms)]['lineIndex']"
    )
    val answers = linesJ.toString().substringAfterLast("[")
            .substringBefore("]").split(",")
    var line = 1
    File(path).forEachLine {
        if (answers.contains(line.toString()))
            println("   $it")
        line++
    }
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
    var countLine = 1
    val regx = Regex("[^А-Яа-яA-Za-z0-9]")
    var index : Long
    File(file).forEachLine { line ->
        if (line.trim() != "") {
            val wordList: List<String> = line.trim().split(" ")
            // Check every word
            wordList.forEach {
                // Get word's index
                index = dict.findIndex((regx.replace(it, "").toLowerCase()))
                // Create new Word Object or make changes in existing one
                if(index.toInt() != -1) {
                    if (!wordIndex.containsKey(index)){
                        wordIndex[index] = Word(index)
                    }
                    if(it == "хлеб")
                        wordIndex[index]?.category = "еда"
                    wordIndex[index]?.addForm(regx.replace(it, "").toLowerCase())
                    wordIndex[index]?.addPage(pageIndex)
                    wordIndex[index]?.addLine(countLine)
                    wordIndex[index]?.increaseAmount()
                }
            }
            // Count lines for params
            lineIndex++
            if(lineIndex == 45) {
                lineIndex = 1
                pageIndex++
            }
        }
        countLine++
    }
    val jsonString = Gson().toJson(wordIndex.values)
    // Make JSON look pretty
    val gson = GsonBuilder().setPrettyPrinting().create()
    val je: JsonElement = JsonParser().parse(jsonString)
    val prettyJsonString = gson.toJson(je)
    // Write our Object with all words and information about them to a JSON file
    val path = "data/${file.substringAfter("/").substringBefore(".")}TextIndex.json"
    File(path).writeText(prettyJsonString)
}