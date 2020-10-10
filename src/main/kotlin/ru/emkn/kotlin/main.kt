package ru.emkn.kotlin

fun main(args: Array<String>) {
    fun Trie<Char>.insert(string: String, wordIndex: Int) {
        insert(string.toList(), wordIndex)
    }

    fun Trie<Char>.collections(prefix: String, wordIndex: Int): List<String> {
        return listForms(prefix.toList(), wordIndex).map { it.joinToString(separator = "") }
    }


    val trie = Trie<Char>().apply {
        insert("car", 0)
        insert("cars", 0)
        insert("card", 1)
        insert("carSpace", 0)
        insert("cargo", 0)
    }

    println("\nCollections starting with \"car\"")
    val prefixedWithCar = trie.collections("car", 0)
    println(prefixedWithCar)

}

data class TrieNode<Key>(var key: Key?, var parent: TrieNode<Key>?) {
    val children: HashMap<Key, TrieNode<Key>> = HashMap()
    var isTerminating = false
    var wordIndex = 0
}
