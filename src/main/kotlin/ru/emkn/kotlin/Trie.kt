package ru.emkn.kotlin

class Trie<Key> {

    private val root = TrieNode<Key>(key = null, parent = null)

    // Method that adds a given word to Trie
    fun insert(list: List<Key>, wordIndex: Long) {
        var current = root
        list.forEach { element ->
            if (current.children[element] == null) {
                current.children[element] = TrieNode(element, current)
            }
            current = current.children[element]!!
        }
        current.wordIndex = wordIndex
        current.isTerminating = true
    }

    // Method that finds an index of the given word
    fun findIndex(list: List<Key>): Long {
        var current = root

        list.forEach { element ->
            val child = current.children[element] ?: return -1
            current = child
        }
        return if (current.isTerminating) current.wordIndex else -1
    }

    // Methods that return all forms of the word
    private fun listForms(prefix: List<Key>, node: TrieNode<Key>?, wordIndex: Long): List<List<Key>> {
        val results = mutableListOf<List<Key>>()

        if (node?.isTerminating == true) {
            if (node.wordIndex == wordIndex)
                results.add(prefix)
        }

        node?.children?.forEach { (key, node) ->
            results.addAll(listForms(prefix + key, node, wordIndex))
        }

        return results
    }

    fun listForms(prefix: List<Key>, wordIndex: Long): List<List<Key>> {
        var current = root

        prefix.forEach { element ->
            val child = current.children[element] ?: return emptyList()
            current = child
        }

        return listForms(prefix, current, wordIndex)
    }
}

// Node structure
data class TrieNode<Key>(var key: Key?, var parent: TrieNode<Key>?) {
    val children: HashMap<Key, TrieNode<Key>> = HashMap()
    var isTerminating = false
    // Index of the word to determine word forms
    var wordIndex: Long = 0
}