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

    fun contains(list: List<Key>): Boolean {
        var current = root

        list.forEach { element ->
            val child = current.children[element] ?: return false
            current = child
        }

        return current.isTerminating
    }

    private fun collections(prefix: List<Key>, node: TrieNode<Key>?): List<List<Key>> {
        // 1
        val results = mutableListOf<List<Key>>()

        if (node?.isTerminating == true) {
            results.add(prefix)
        }

        // 2
        node?.children?.forEach { (key, node) ->
            results.addAll(collections(prefix + key, node))
        }

        return results
    }

    fun collections(prefix: List<Key>): List<List<Key>> {
        // 1
        var current = root

        prefix.forEach { element ->
            val child = current.children[element] ?: return emptyList()
            current = child
        }

        // 2
        return collections(prefix, current)
    }
}