package ru.emkn.kotlin

fun main(args: Array<String>) {
    fun Trie<Char>.insert(string: String, wordIndex: Long) {
        insert(string.toList(), wordIndex)
    }

    fun Trie<Char>.collections(prefix: String, wordIndex: Long): List<String> {
        return listForms(prefix.toList(), wordIndex).map { it.joinToString(separator = "") }
    }

    val trie = Trie<Char>().apply {
        insert("car", 0)
    }
    trie.apply {
        insert("card", 1)
    }
    trie.apply {
        insert("cards", 1)
    }
    trie.apply {
        insert("cars", 0)
    }
    trie.apply {
        insert("carPark", 0)
    }

    println("\nCollections starting with \"car\"")
    val prefixedWithCar = trie.collections("car", 0)
    println(prefixedWithCar)

}

data class TrieNode<Key>(var key: Key?, var parent: TrieNode<Key>?) {
    val children: HashMap<Key, TrieNode<Key>> = HashMap()
    var isTerminating = false
    var wordIndex: Long = 0
}
