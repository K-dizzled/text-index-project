package ru.emkn.kotlin

fun main(args: Array<String>) {
    fun Trie<Char>.findIndex(string: String): Long {
        return findIndex(string.toList())
    }

    fun Trie<Char>.listForms(prefix: String, wordIndex: Long): List<String> {
        return listForms(prefix.substring(0,(prefix.length / 2)).toList(), wordIndex).map { it.joinToString(separator = "") }
    }

    val trie = parseCSV()

    println("\nСловоформы слова \"коряга\"")
    println(trie.listForms("коряга", trie.findIndex("коряга")))
}

