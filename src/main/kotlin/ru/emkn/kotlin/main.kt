package ru.emkn.kotlin

import java.io.File
import kotlin.collections.HashSet

fun main(args: Array<String>) {
    val wordToBeFound = "хлеб".toLowerCase()
    fun Trie<Char>.findIndex(string: String): Long {
        return findIndex(string.toList())
    }

    fun Trie<Char>.listForms(prefix: String, wordIndex: Long): List<String> {
        return listForms(prefix.substring(0,(prefix.length / 2)).toList(), wordIndex)
            .map { it.joinToString(separator = "") }
    }

    val trie = parseCSV()

    //println(trie.listForms(wordToBeFound, trie.findIndex(wordToBeFound)))
    val answer1= findWordInText(trie.listForms(wordToBeFound,
        trie.findIndex(wordToBeFound)).toList())
    println("Страницы, на которых найдены формы слова \"$wordToBeFound\": ${answer1.joinToString()}")
    findWordInText(trie.listForms(wordToBeFound, trie.findIndex(wordToBeFound)), task = 2)
}

fun findWordInText(wordForms: List<String>, fileName: String = "./data/Childhood.txt", task: Int = 1)
        : HashSet<Int> {
    var pageIndex = 1
    var lineIndex = 1
    var formsCounter : Long = 0
    val pagesWhereWordFormAppears = hashSetOf<Int>()
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
    return pagesWhereWordFormAppears
}