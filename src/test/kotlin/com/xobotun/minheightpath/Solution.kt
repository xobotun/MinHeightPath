package com.xobotun.minheightpath

import com.xobotun.minheightpath.TestCase.Companion.multiple
import com.xobotun.minheightpath.TestCase.Companion.singular
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.reflect.KClass
import kotlin.test.assertContentEquals
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

class SolutionTest {
    private val pathfinders: List<KClass<out Solution>> = listOf(
        // FailingSolution::class,
        OnePathHashSolution::class
    )
    private val testExamples = listOf(
        // Borderline cases
        singular(emptyList(), emptyList()),
        singular(listOf(1), listOf(1)),
        singular(listOf(1, 1), listOf(1, 1)),

        // Whole sequence is a path
        singular(listOf(1, 1, 1, 1), listOf(1, 1, 1, 1)),
        singular(listOf(1, 2, 2, 1), listOf(1, 2, 2, 1)),
        singular(listOf(1, 1, 1, 2), listOf(1, 1, 1, 2)),

        // First sequence is the longest
        singular(listOf(1, 1, 1, 2, 3, 3), listOf(1, 1, 1, 2)),

        // Second sequence is the longest
        singular(listOf(1, 1, 2, 3, 3, 3), listOf(2, 3, 3, 3)),

        // Some sequences of the same length
        multiple(listOf(1, 1, 1, 2, 2, 2), listOf(listOf(1, 1, 1, 2), listOf(1, 2, 2, 2))),
        multiple(listOf(1, 2, 3, 4, 5, 6), listOf(listOf(1, 2), listOf(2, 3), listOf(3, 4), listOf(4, 5), listOf(5, 6))),

        // The original test
        singular(listOf(3, 4, 5, 6, 7, 7, 7, 6, 5, 5, 6), listOf(6, 7, 7, 7, 6)),
    )

    @TestFactory
    fun runPathfinders() = runCases(testExamples)

    @TestFactory
    fun runCsvPathfinders() = runCases(makeCasesFromCsv())

    private fun runCases(cases: List<TestCase>) = pathfinders.map { it.construct() }.flatMap { pathfinder -> cases.map { pathfinder to it } }.map {
        DynamicTest.dynamicTest("[${it.first::class.simpleName}] #${it.second.number}: ${it.second.input} must yield ${it.second.expected.size} solutions: ${it.second.expected}. Necessary to pass: ${it.second.isNecessary}") {
            val actual = it.first.solve(it.second.input)

            try {
                assertContentEquals(it.second.expected, actual, "Expected: ${it.second.expected}. Actual: $actual")
            } catch (e: AssertionError) {
                if (it.second.allowPartial && it.second.expected.isNotEmpty() && actual.isNotEmpty()) {
                    try {
                        assertContentEquals(it.second.expected[0], actual[0], "Expected partial: ${it.second.expected}. Actual: $actual")
                    } catch (e2: AssertionError) {
                        if (it.second.isNecessary) throw e
                        print("${it.second.number} has failed, but was not necessary")
                    }
                    return@dynamicTest
                }

                if (it.second.isNecessary) throw e
                print("${it.second.number} has failed, but was not necessary")
            }
        }
    }

    private fun makeCasesFromCsv(): List<TestCase> {
        val result = ArrayList<TestCase>(1000)
        csvReader().open(this.javaClass.classLoader.getResourceAsStream("data.csv")!!) {
            generateSequence { readNext() }.forEachIndexed() { index, row ->
                val input = row[0]
                val expected = row[1]
                result.add(TestCase(index, input.toInts(), listOf(expected.toInts()), allowPartial = true))
            }
        }
        return result
    }

    private fun String.toInts() = subSequence(1..length-1-1).split(",").map { it.trim() }.filter { it.isNotEmpty() }.map { it.toInt() }
}

private data class TestCase(
    val number: Int = nextTestNumber++,
    val input: List<Int>,
    val expected: List<Path>,
    val isNecessary: Boolean = true,
    val allowPartial: Boolean = false,
) {
    companion object {
        private var nextTestNumber = 0

        fun singular(
            input: List<Int>,
            expected: Path
        ) = TestCase(nextTestNumber++, input, listOf(expected))

        fun multiple(
            input: List<Int>,
            expected: List<Path>,
            isNecessary: Boolean = false
        ) = TestCase(nextTestNumber++, input, expected, isNecessary)
    }
}

/**
 * Attempts to call a no-arg constructor
 */
private fun <T : Any> KClass<T>.construct() = constructors.first { it.parameters.isEmpty() }.call()
