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

fun findWordInText(wordForms: List<String>, fileName: String, task: Int = 1)
        : Triple<HashSet<Int>, HashSet<String>, Long> {
    var pageIndex = 1
    var lineIndex = 1
    var formsCounter : Long = 0
    val pagesWhereWordFormAppears = hashSetOf<Int>()
    val wordFormsThatAppear = hashSetOf<String>()
    // Regex used to remove punctuation marks etc.
    val regx = Regex("[^А-Яа-яA-Za-z0-9]")
    File(fileName).forEachLine { line ->
        if (line.trim() != "") {
            val wordList: List<String> = line.trim().split(" ")
            wordList.forEach {
                // If the list of given word forms contains the word
                // (with unwanted regex removed)
                // we find in the text, then we add it to our answer list
                if (wordForms.contains(regx.replace(it, "").toLowerCase())) {
                    // If task number equals 2, we print the whole line
                    if(task == 3)
                        println(line)
                    else
                        pagesWhereWordFormAppears.add(pageIndex)
                    wordFormsThatAppear.add(regx.replace(it, "").toLowerCase())
                    formsCounter++
                }
            }
            lineIndex++
            if(lineIndex == 45) {
                lineIndex = 1
                pageIndex++
            }
        }
    }
    return Triple(pagesWhereWordFormAppears, wordFormsThatAppear, formsCounter)
}

fun makeListOfTopUsedWords(dict: Trie<Char>, fileName: String)
        : MutableMap<Long, Int> {

    fun Trie<Char>.findIndex(string: String): Long {
        return findIndex(string.toList())
    }

    val mostUsedWords = mutableMapOf<Long, Int>()
    File(fileName).forEachLine { line ->
        if (line.trim() != "") {
            val wordList: List<String> = line.trim().split(" ")
            // Here we build a map of <Word Index, Amount of times it was found in text>.
            // As we use indexes, not words, we roughly count each word "first form"
            // occurrences in the text
            wordList.forEach {
                val currentWordIndex = dict.findIndex(it)
                if(!mostUsedWords.containsKey(currentWordIndex))
                    mostUsedWords[currentWordIndex] = 1
                else
                    mostUsedWords[currentWordIndex] = mostUsedWords[currentWordIndex]!! + 1
            }
        }
    }
    return mostUsedWords
}

fun printMostUsedWords(dict: Trie<Char>, fileName: String, topNWords: Int) : MutableList<String> {
    // Sort Map by word occurrences
    val answer2 = makeListOfTopUsedWords(dict, fileName).toList().sortedByDescending { it.second }
    var counter = 0
    var word = 0
    val topOfWords = mutableListOf<String>()
    // Parse odict to a list, so you can easily take any of its lines
    val listDict = parseIntoList()
    while (counter < topNWords) {
        // If the word doesn't appear in odict, its index is set to -1
        if(answer2[word].first.toInt() != -1) {
            // Parse line of list
            var ffWord = listDict[answer2[word].first.toInt() - 1]
                .substringAfter("values=[")
            // As sometimes in odict the second word indicates form of the word, we check
            // its length to be more than 4
            val check = ffWord.substringAfter(", ").substringBefore(",")
            ffWord = ffWord.substringBefore(",")
            // Also we want the word's length to be more than 4,
            // to skip prepositions, particles etc.
            if((ffWord.length > 4) && (check.length > 4)) {
                topOfWords.add(ffWord)
                counter++
            }
        }
        word++
        if (word >= answer2.size) {
            println("Error: Указанное число желаемых слов превышает максимальное возможное")
            return topOfWords
        }
    }
    return topOfWords
}

fun findWordsFromCategory(category: String, dict: Trie<Char>, fileName: String)
        : HashSet<String> {

    fun Trie<Char>.findIndex(string: String): Long {
        return findIndex(string.toList())
    }

    fun Trie<Char>.listForms(prefix: String, wordIndex: Long): List<String> {
        return listForms(prefix.substring(0, (prefix.length / 2)).toList(), wordIndex)
            .map { it.joinToString(separator = "") }
    }

    val wordsFromCategory = hashSetOf<String>()
    var neededList = listOf<String>()
    // File with words listed by category
    File("./data/categories.txt").forEachLine { Category ->
           if(Category.startsWith(category)) {
               neededList = Category.substringAfter(": ").trim().split(" ")
        }
    }
    // The algorithm below is the same as with looking
    // for the word forms, the only difference is that
    // we check not only does the word appear in the given
    // list of wordForms, but also in all such wordLists
    // of the words from one category
    val regx = Regex("[^А-Яа-яA-Za-z0-9]")
    val wordForms = mutableListOf<String>()
    for(word in neededList) {
        wordForms.addAll(dict.listForms(word, dict.findIndex(word)))
    }
    File(fileName).forEachLine { line ->
        val wordList: List<String> = line.trim().split(" ")
        wordList.forEach {
            if (wordForms.contains(regx.replace(it, "").toLowerCase())) {
                wordsFromCategory.add(regx.replace(it, "").toLowerCase())
            }
        }
    }
    return wordsFromCategory
}


class Interface : CliktCommand() {
    private val task: Int by option(help = "Task number").int().default(1)
    private val word: String by option(help = "Word to look for").default("ужин")
    private val category: String by option(help = "Category to look for").default("еда")
    private val input: String by option(help = "Path for input text").default("./data/Childhood.txt")
    private val top: Int by option(help = "Define amount of most used words needed").int().default(5)

    override fun run() {
        val path = "data/${input.substringAfterLast("/").substringBefore(".")}TextIndex.json"
        if (File(input).exists()) {
            if (!File(path).exists())
                analyzeText(path)
        }
        else
            println("Error: Имя файла с входными данными или слово указаны неверно, проверьте входные данные")
        val json = String(Files.readAllBytes(Paths.get(path)))
        val document: Any = Configuration.defaultConfiguration().jsonProvider().parse(json)
        if (word.all { it.isLetter() }) {
            when(task) {
                1 -> task1(word, document)
                2 -> task2(word, category, input, top)
                3 -> task3(word, document, input)
                else -> println("Некорректный номер задания")
            }
        }
        else
            println("Error: Слово указано неверно, проверьте входные данные")
    }

    // Functions to simplify Trie methods calls
    private fun Trie<Char>.findIndex(string: String): Long {
        return findIndex(string.toList())
    }
    // Function searches for word forms of the word.
    // It takes the first half of the word as prefix
    // so that the word forms with length
    // less than word's also count
    private fun Trie<Char>.listForms(prefix: String, wordIndex: Long): List<String> {
        return listForms(prefix.substring(0, (prefix.length / 2)).toList(), wordIndex)
                .map { it.joinToString(separator = "") }
    }

    private fun task1(wordToBeFound: String, document: Any) {
        val pages
                = JsonPath.read<List<Map<String, Any>>>(
            document,
            "$[?('$wordToBeFound'in @.forms)]['pageIndex']"
        )
        val answer = pages.toString().substringAfterLast("[")
            .substringBefore("]").split(",").joinToString()
        println("Страницы, на которых найдены формы слова \"$wordToBeFound\": $answer")
    }

    private fun task2(wordToBeFound: String, category: String, inputFile: String, topUsedWordsListSize: Int) {
        // Create a Trie object, where we store a dictionary of word forms
        val dict = parseCSV()

        // List the numbers of pages, where the word or it's forms where used
        val answer1= findWordInText(
            dict.listForms(
                wordToBeFound,
                dict.findIndex(wordToBeFound)
            ).toList(), fileName = inputFile
        )
        println("Страницы, на которых найдены формы слова \"$wordToBeFound\": ${answer1.first.joinToString()}")
        // Forms of the word, that were used in the text
        println("Формы, в которых встречается слово \"$wordToBeFound\": ${answer1.second.joinToString()}")
        // Amount of times the word or it's forms were used
        println("Количество раз, когда встретилось слово \"$wordToBeFound\": ${answer1.third}")
        // Print 5 most used words from the text
        val topNWords = printMostUsedWords(dict, fileName = inputFile, topNWords = topUsedWordsListSize)
        println("Топ $topUsedWordsListSize самых часто встречающихся в тексте слов: ${topNWords.joinToString()}")
        // Print words from given category
        val wordsFromCategory = findWordsFromCategory(category, dict, fileName = inputFile).joinToString()
        if(wordsFromCategory.isEmpty())
            println("Error: Указанная категория не найдена, проверьте входные данные")
        else
            println("Формы, в которых встречаются слова из категории \"$category\": $wordsFromCategory")
    }

    private fun task3(wordToBeFound: String, document: Any, path: String) {
        val pages
                = JsonPath.read<List<Map<String, Any>>>(
            document,
            "$[?('$wordToBeFound'in @.forms)]['lineIndex']"
        )
        val answers = pages.toString().substringAfterLast("[")
            .substringBefore("]").split(",")
        val lines = File(path).readLines()
        for (answer in answers )
            println("   ${lines[answer.toInt()]}")
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
            wordList.forEach {
                index = dict.findIndex((regx.replace(it, "").toLowerCase()))
                if(index.toInt() != -1) {
                    if (!wordIndex.containsKey(index)){
                        wordIndex[index] = Word(index)
                    }
                    wordIndex[index]?.addForm(regx.replace(it, "").toLowerCase())
                    wordIndex[index]?.addPage(pageIndex)
                    wordIndex[index]?.addLine(countLine)
                    wordIndex[index]?.increaseAmount()
                }
            }
            lineIndex++
            if(lineIndex == 45) {
                lineIndex = 1
                pageIndex++
            }
        }
        countLine++
    }
    val jsonString = Gson().toJson(wordIndex.values)
    val gson = GsonBuilder().setPrettyPrinting().create()
    val je: JsonElement = JsonParser().parse(jsonString)
    val prettyJsonString = gson.toJson(je)
    val path = "data/${file.substringAfter("/").substringBefore(".")}TextIndex.json"
    File(path).writeText(prettyJsonString)
}