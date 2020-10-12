package ru.emkn.kotlin

import java.io.File
import kotlin.collections.HashSet

fun main(args: Array<String>) {
    val wordToBeFound = "ужин".toLowerCase()

    //Functions to simplify Trie methods calls
    fun Trie<Char>.findIndex(string: String): Long {
        return findIndex(string.toList())
    }

    fun Trie<Char>.listForms(prefix: String, wordIndex: Long): List<String> {
        return listForms(prefix.substring(0,(prefix.length / 2)).toList(), wordIndex)
            .map { it.joinToString(separator = "") }
    }

    //Create a Trie object, where we store a dictionary of word forms
    val dict = parseCSV()

    //List the numbers of pages, where the word or it's forms where used
    val answer1= findWordInText(dict.listForms(wordToBeFound,
        dict.findIndex(wordToBeFound)).toList())
    println("Страницы, на которых найдены формы слова \"$wordToBeFound\": ${answer1.first.joinToString()}")
    //Forms of the word, that were used in the text
    println("Формы, в которых встречается слово \"$wordToBeFound\": ${answer1.second.joinToString()}")
    //Amount of times the word or it's forms were used
    println("Количество раз, когда встретилось слово \"$wordToBeFound\": ${answer1.third}")
    //Prints all lines, where the word or it's forms were used
    findWordInText(dict.listForms(wordToBeFound, dict.findIndex(wordToBeFound)), task = 2)
    //Print 5 most used words from the text
    printMostUsedWords(dict)
    //Print all words and it's forms contained in some category
    val category = "прилагательные"
    println("Формы, в которых встречаются слова из категории \"$category\": " +
            findWordsFromCategory(category, dict).joinToString()
    )
}

fun findWordInText(wordForms: List<String>, fileName: String = "./data/Childhood.txt", task: Int = 1)
        : Triple<HashSet<Int>, HashSet<String>, Long> {
    var pageIndex = 1
    var lineIndex = 1
    var formsCounter : Long = 0
    val pagesWhereWordFormAppears = hashSetOf<Int>()
    val wordFormsThatAppear = hashSetOf<String>()
    val regx = Regex("[^А-Яа-яA-Za-z0-9]")
    File(fileName).forEachLine { line ->
        if (line.trim() != "") {
            val wordList: List<String> = line.trim().split(" ")
            wordList.forEach {
                if (wordForms.contains(regx.replace(it, "").toLowerCase())) {
                    if(task == 2)
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

fun makeListOfTopUsedWords(dict: Trie<Char>, fileName: String = "./data/Childhood.txt")
        : MutableMap<Long, Int> {

    fun Trie<Char>.findIndex(string: String): Long {
        return findIndex(string.toList())
    }

    val mostUsedWords = mutableMapOf<Long, Int>()
    File(fileName).forEachLine { line ->
        if (line.trim() != "") {
            val wordList: List<String> = line.trim().split(" ")
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

fun printMostUsedWords(dict: Trie<Char>) {
    val answer2 = makeListOfTopUsedWords(dict).toList().sortedByDescending { it.second }
    var counter = 0
    var word = 0
    val topOfWords = mutableListOf<String>()
    val listDic = parseIntoList()
    while (counter < 5) {
        if(answer2[word].first.toInt() != -1) {
            var ffWord = listDic[answer2[word].first.toInt() - 1]
                .substringAfter("values=[")
            val check = ffWord.substringAfter(", ").substringBefore(",")
            ffWord = ffWord.substringBefore(",")
            if((ffWord.length > 4) && (check.length > 4)) {
                topOfWords.add(ffWord)
                counter++
            }
        }
        word++
    }
    println("Топ 5 самых часто встречающихся в тексте слов: ${topOfWords.joinToString()}")
}

fun findWordsFromCategory(category: String, dict: Trie<Char>, fileName: String = "./data/Childhood.txt")
        : HashSet<String> {

    fun Trie<Char>.findIndex(string: String): Long {
        return findIndex(string.toList())
    }

    fun Trie<Char>.listForms(prefix: String, wordIndex: Long): List<String> {
        return listForms(prefix.substring(0,(prefix.length / 2)).toList(), wordIndex)
            .map { it.joinToString(separator = "") }
    }
    val wordsFromCategory = hashSetOf<String>()
    var neededList = listOf<String>()
    File("./data/categories.txt").forEachLine { Category ->
           if(Category.startsWith(category)) {
               neededList = Category.substringAfter(": ").trim().split(" ")
        }
    }
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