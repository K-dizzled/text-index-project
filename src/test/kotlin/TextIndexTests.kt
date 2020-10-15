import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.emkn.kotlin.Trie
import ru.emkn.kotlin.findWordInText
import ru.emkn.kotlin.parseCSV
import ru.emkn.kotlin.printMostUsedWords
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.test.assertEquals

class TextIndexTests {
    private fun Trie<Char>.findIndexTest(string: String): Long {
        return findIndex(string.toList())
    }

    private fun Trie<Char>.listFormsTest(prefix: String, wordIndex: Long): List<String> {
        return listForms(prefix.substring(0,(prefix.length / 2)).toList(), wordIndex)
            .map { it.joinToString(separator = "") }
    }

    private val dict = parseCSV()

    @Test
    fun `find word in text`() {
        assertEquals(hashSetOf(65, 66, 39, 8, 72, 10, 12, 19, 20, 24, 26, 58, 29, 30, 62),
            findWordInText(listOf("отец", "отца", "отцом", "отцу"), fileName = "./data/Childhood.txt").first)
        assertEquals(hashSetOf(10, 12),
            findWordInText(listOf("хлебу", "хлеб", "хлебом", "хлеба"), fileName = "./data/Childhood.txt").first)
        assertEquals(hashSetOf(),
            findWordInText(listOf("утка", "уткой", "утки", "утке"), fileName = "./data/Childhood.txt").first)

        assertEquals(hashSetOf("пространства", "пространство", "пространстве"),
            findWordInText(listOf("пространства", "пространство", "пространством", "пространстве"),
                fileName = "./data/textForTests.txt").second)
        assertEquals(hashSetOf("куба"),
            findWordInText(listOf("куб", "кубу", "кубом", "куба"), fileName = "./data/textForTests.txt").second)
        assertEquals(hashSetOf(),
            findWordInText(listOf("многогранник", "многогранником", "многогранники", "многограннике"),
                fileName = "./data/textForTests.txt").second)

        assertEquals(20,
            findWordInText(listOf("троллей", "тролль", "троллю", "тролля", "тролли", "троллями"),
                fileName = "./data/Mumintroll.txt").third)
        assertEquals(20,
            findWordInText(listOf("папы", "папе", "папу", "папа"), fileName = "./data/Mumintroll.txt").third)
        assertEquals(0,
            findWordInText(listOf("комета", "кометой", "кометы"), fileName = "./data/Mumintroll.txt").third)

    }

//    @Test
//    fun `illegal argument test`() {
//        assertFailsWith(IllegalArgumentException::class) {
//            //main(-2)
//        }
//    }

    @TestFactory
    fun `top of words multiple test`() : Stream<DynamicTest> {
        val texts = listOf("./data/Childhood.txt", "./data/Mumintroll.txt", "./data/textForTests.txt")
        val expected = mutableListOf<MutableList<String>>()
        expected.add(mutableListOf("чтобы", "только", "ничего",
            "будто", "перед", "первый", "должно", "около", "через", "подле"))
        expected.add(mutableListOf("чтобы", "ничего", "дальше",
            "вокруг", "словно", "через", "всего", "только", "среди", "можно"))
        expected.add(mutableListOf("чтобы", "более", "первый",
            "можно", "сотый", "через", "менее", "должно", "фиксировать", "ничего"))

        return IntStream.range(0, 3).mapToObj { n ->
            DynamicTest.dynamicTest("Test top for $n text") {
                assertEquals(expected[n], printMostUsedWords(dict, texts[n], 10))
            }
        }
    }


    @TestFactory
    fun `words count multiple test`(): Stream<DynamicTest> {
        val words = listOf("парень", "модуль", "собака", "квадрат", "площадь",
            "круг", "вершина", "предел", "производная")
        val forms = mutableListOf<List<String>>()
        for (word in words) {
            forms.add(dict.listFormsTest(word, dict.findIndexTest(word)))
        }

        val expected: List<Long> = listOf(0, 0, 0, 2, 1, 5, 3, 1, 6)
        return IntStream.range(0, 9).mapToObj { n ->
            DynamicTest.dynamicTest("Test word count for $n word") {
                assertEquals(expected[n], findWordInText(forms[n],
                    fileName = "./data/textForTests.txt").third)
            }
        }
    }
}