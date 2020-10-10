package ru.emkn.kotlin

class Trie<Key> {

    private val root = TrieNode<Key>(key = null, parent = null)

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

    private fun listForms(prefix: List<Key>, node: TrieNode<Key>?, wordIndex: Long): List<List<Key>> {
        val results = mutableListOf<List<Key>>()

        if (node?.isTerminating == true) {
            //if (node.wordIndex == wordIndex)
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

data class TrieNode<Key>(var key: Key?, var parent: TrieNode<Key>?) {
    val children: HashMap<Key, TrieNode<Key>> = HashMap()
    var isTerminating = false
    var wordIndex: Long = 0
}