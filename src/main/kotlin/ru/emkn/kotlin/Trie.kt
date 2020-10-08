package ru.emkn.kotlin

class Trie<Key> {

    private val root = TrieNode<Key>(key = null, parent = null)

    fun insert(list: List<Key>) {
        var current = root

        list.forEach { element ->
            if (current.children[element] == null) {
                current.children[element] = TrieNode(element, current)
            }
            current = current.children[element]!!
        }

        current.isTerminating = true
    }

    private fun listForms(prefix: List<Key>, node: TrieNode<Key>?): List<List<Key>> {
        val results = mutableListOf<List<Key>>()

        if (node?.isTerminating == true) {
            results.add(prefix)
        }

        node?.children?.forEach { (key, node) ->
            results.addAll(listForms(prefix + key, node))
        }

        return results
    }

    fun listForms(prefix: List<Key>): List<List<Key>> {
        var current = root

        prefix.forEach { element ->
            val child = current.children[element] ?: return emptyList()
            current = child
        }

        return listForms(prefix, current)
    }
}