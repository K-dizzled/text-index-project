package ru.emkn.kotlin

fun main(args: Array<String>) {
    fun Trie<Char>.insert(string: String) {
        insert(string.toList())
    }

    fun Trie<Char>.contains(string: String): Boolean {
        return contains(string.toList())
    }

    fun Trie<Char>.collections(prefix: String): List<String> {
        return collections(prefix.toList()).map { it.joinToString(separator = "") }
    }


    val trie = Trie<Char>().apply {
        insert("car")
        insert("card")
        insert("care")
        insert("cared")
        insert("cars")
        insert("carbs")
        insert("carapace")
        insert("cargo")
    }

    println("\nCollections starting with \"car\"")
    val prefixedWithCar = trie.collections("car")
    println(prefixedWithCar)

}

class TrieNode<Key>(var key: Key?, var parent: TrieNode<Key>?) {
    val children: HashMap<Key, TrieNode<Key>> = HashMap()
    var isTerminating = false
}