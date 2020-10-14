import org.junit.jupiter.api.*
import ru.emkn.kotlin.*
import java.lang.IllegalArgumentException
import java.time.Duration
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TextIndexTests {
    @Test
    fun `Find word in text`() {
//      assertEquals(2, main(2))
        //val testWordForms = listOf("ìàìàøà", "ìàìàøó")


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