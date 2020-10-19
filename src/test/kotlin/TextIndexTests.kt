import com.jayway.jsonpath.Configuration
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import ru.emkn.kotlin.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.test.assertEquals

class TextIndexTests {
    @Test
    fun `find word in text1`() {
        val path = "data/ChildhoodTextIndex.json"
        if (!File(path).exists())
            analyzeText("data/Childhood.txt")
        val json = String(Files.readAllBytes(Paths.get(path)))
        val document: Any = Configuration.defaultConfiguration().jsonProvider().parse(json)
        assertEquals(
            listOf("8", "10", "12", "19", "20", "24", "26", "29", "30", "39", "58", "62", "65", "66", "72"),
            task1("отец", document)
        )
        assertEquals(
            listOf("1", "36", "51"),
            task1("лев", document)
        )
        assertEquals(
            listOf(""),
            task1("утка", document)
        )
    }
    @Test
    fun `find word in text2`() {
        val path1 = "data/textForTestsTextIndex.json"
        if (!File(path1).exists())
            analyzeText("data/textForTests.txt")
        val json1 = String(Files.readAllBytes(Paths.get(path1)))
        val document1: Any = Configuration.defaultConfiguration().jsonProvider().parse(json1)
        assertEquals(
            listOf(""),
            task1("модуль", document1)
        )
        assertEquals(
            listOf(""),
            task1("дробь", document1)
        )
        assertEquals(
            listOf("1", "2", "8"),
            task1("производной", document1)
        )
    }
    @Test
    fun `find word in text3`() {
        val path2 = "data/MumintrollTextIndex.json"
        if (!File(path2).exists())
            analyzeText("data/Mumintroll.txt")
        val json2 = String(Files.readAllBytes(Paths.get(path2)))
        val document2: Any = Configuration.defaultConfiguration().jsonProvider().parse(json2)
        assertEquals(
            listOf("2", "3", "4", "5"),
            task1("тролль", document2)
        )
        assertEquals(
            listOf(""),
            task1("шапка", document2)
        )
        assertEquals(
            listOf("1", "3", "4", "6"),
            task1("лодка", document2)
        )
    }

    @TestFactory
    fun `words count multiple test`(): Stream<DynamicTest> {
        val path = "data/ChildhoodTextIndex.json"
        val json = String(Files.readAllBytes(Paths.get(path)))
        val document: Any = Configuration.defaultConfiguration().jsonProvider().parse(json)
        val words = listOf("парень", "модуль", "собака", "квадрат", "площадь",
            "пошел", "вершина", "готов", "ужин")

        val expected: List<Long> = listOf(1, 0, 20, 0, 0, 0, 0, 12, 5)
        return IntStream.range(0, 9).mapToObj { n ->
            DynamicTest.dynamicTest("Test word count for $n word") {
                assertEquals(expected[n], task2(words[n], "еда", document, 1))
            }
        }
    }
}