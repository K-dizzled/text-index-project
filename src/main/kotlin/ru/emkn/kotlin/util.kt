//package ru.emkn.kotlin
//
//import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
//import java.io.File
//import java.io.FileReader
//
//
//fun main(args: Array<String>) {
////    val csvData: String = "a,b,c\nd,e,f\ne,e,e,f"
////    val file: File = File(args[0])
////    val rows: List<Map<String, String>> = csvReader().readAllWithHeader(csvData)
////    println(rows.toString().encodeToByteArray()) //[{a=d, b=e, c=f}]
//    val words = listOf(
//        "hello",
//        "help",
//        "helicopter",
//        "hero",
//        "hope",
//        "echo",
//        "hotelHop",
//        "hotel",
//        "hot",
//        "hop"
//    )
//    val trie = Trie(words)
//    for (word in trie) {
//        println(word)
//    }
//    println(trie has "helicopter")
//    trie.listChildren()
//}
//class Trie(words: Iterable<String>) : Iterable<String> {
//    //Текущий набор нод
//    private val nodes = mutableListOf<TrieNode>()
//
//    private fun addWord(nodes: MutableList<TrieNode>, word: String) {
//        //Если слово пустое и ни один из элементов листа нод не
//        //является конечной нодой, то добавляется конечная нода
//        if (word.isEmpty() && nodes.none { it === EndNode }) {
//            nodes.add(EndNode)
//            return
//        }
//
//        val letter = word.first()
//        val rest = word.drop(1)
//
//        val child = nodes.find {
//            when (it) {
//                is ValueNode -> it.value == letter
//                else -> false
//            }
//        }
//
//        when (child) {
//            is ValueNode -> addWord(child.nodes, rest)
//            else -> {
//                val nextNode = ValueNode(letter)
//                addWord(nextNode.nodes, rest)
//                nodes.add(nextNode)
//            }
//        }
//    }
//
//    private fun collectWords(nodes: List<TrieNode>, word: String): Sequence<String> {
//        return sequence {
//            val sortedNodes = nodes.sortedBy {
//                when (it) {
//                    is ValueNode -> it.value
//                    is EndNode -> ' '
//                }
//            }
//
//            for (node in sortedNodes) {
//                when (node) {
//                    is ValueNode -> {
//                        val nextWord = word + node.value
//                        println("$node 999")
//                        yieldAll(collectWords(node.nodes, nextWord))
//                    }
//                    is EndNode -> yield(word)
//                }
//            }
//        }
//    }
//
//    infix fun has(word: String): Boolean {
//
//        fun inNodes(nodes: List<TrieNode>, word: String): Boolean {
//            if (word.isEmpty()) {
//                return nodes.any { it === EndNode }
//            }
//
//            val firstLetter = word.first()
//
//            val child = nodes.find {
//                when (it) {
//                    is ValueNode -> it.value == firstLetter
//                    else -> false
//                }
//            }
//            return when (child) {
//                is ValueNode -> inNodes(child.nodes, word.drop(1))
//                else -> false
//            }
//        }
//
//        return inNodes(nodes, word)
//    }
//
//    fun listChildren() {
//        println(collectWords(nodes, "hel").toList())
//    }
//
//    override fun iterator(): Iterator<String> {
//        return collectWords(nodes, "").iterator()
//    }
//
//    init {
//        for (word in words) {
//            addWord(nodes, word)
//        }
//    }
//}
//
//// Add to TrieNode.kt:
//sealed class TrieNode
//data class ValueNode(val value: Char): TrieNode() {
//    val nodes = mutableListOf<TrieNode>()
//}
//object EndNode: TrieNode()
