import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.emkn.kotlin.Trie
import ru.emkn.kotlin.findWordInText
import ru.emkn.kotlin.parseCSV
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.test.assertEquals

class TextIndexTests {
    @Test
    fun `Find word in text`() {
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
//
//    @Test
//    fun `timeout test`() {
//        val res = assertTimeoutPreemptively(Duration.ofSeconds(5)) {
//            //main()
//            //main(50)
//        }
//        assertEquals(20365011074L, res)
//    }

    @TestFactory
    fun `multiple test`(): Stream<DynamicTest> {
        val expected: List<Long> = listOf(1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89)
        return IntStream.range(0, 11).mapToObj { n ->
            DynamicTest.dynamicTest("Test fib for $n") {
                //assertEquals(expected[n], main(n))
            }
        }
    }
}