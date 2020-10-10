package ru.emkn.kotlin

fun main(args: Array<String>) {
    fun Trie<Char>.insert(string: String, wordIndex: Long) {
        insert(string.toList(), wordIndex)
    }

    fun Trie<Char>.collections(prefix: String, wordIndex: Long): List<String> {
        return listForms(prefix.toList(), wordIndex).map { it.joinToString(separator = "") }
    }

    val trie = parseCSV()

    println("\nСловоформы слова \"коряга\"")
    val prefixedWithCar = trie.collections("кор", 1)
    println(prefixedWithCar)

}

