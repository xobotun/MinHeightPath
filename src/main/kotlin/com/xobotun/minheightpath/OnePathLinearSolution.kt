package com.xobotun.minheightpath

/**
 * Pathfinder that returns only the first longest path.
 * Takes O(1) additional memory, has O(n) time complexity.
 */
class OnePathLinearSolution: Solution {

    override fun solve(mountainRange: List<Int>): List<Path> {
        if (mountainRange.isEmpty()) return listOf(emptyList())

        var pathStartIndex = 0 // Index of the current path start
        var pathDeviationStartIndex = 0 // Index of the first value in a non-interrupted sequence that differs from mountainRange[pathStartIndex]
                                        // E.g. in the end, it will be here: 1112211[2]2111
        var pathDeviation = 0 // -1..+1. When goes out of range, pathStartIndex should be reset to pathDeviationStartIndex

        var longestPathStartIndex = -1
        var longestPathEndIndex = -1   // Inclusive
        val getBestLength = { longestPathEndIndex - longestPathStartIndex + 1 }
        val isLonger = {start: Int, end: Int -> end - start + 1 > getBestLength() }

        for (i in 1..mountainRange.size - 1) {
            when {
                mountainRange[pathStartIndex] == mountainRange[i] -> { /* Do nothing, it's a straight path */ }
                mountainRange[pathStartIndex] + pathDeviation == mountainRange[i] -> { /* Do nothing, it's an almost straight path */ }
                mountainRange[pathStartIndex] != mountainRange[i] -> {
                    // Now, when we enter this branch, it means one of two things has happened:
                    //  • either we previously were on a flat sequence and entered a new slightly lowered or heightened sequence
                    //    E.g. 1111[2]222
                    if (pathDeviation == 0) {
                        // Calculate whether we started deviating into +1 or -1 range. Also start tracking the index when the deviation started
                        pathDeviation = mountainRange[i] - mountainRange[pathStartIndex] // 111[2]2 yields 2-1 = +1 here
                        pathDeviationStartIndex = i
                    }
                    //  • or we have entered second sequence that differs from initial sequence by more than one
                    //    E.g. 11112222[3]333
                    else {
                        // Check if it was the longest path
                        if (isLonger(pathStartIndex, i)) {
                            longestPathStartIndex = pathStartIndex
                            longestPathEndIndex = i - 1
                        }
                        // Start tracking the path since the last deviation
                        pathStartIndex = pathDeviationStartIndex
                        // Same two lines as in "then" branch. Why didn't I moved them out of this condition? For the sake of readability! Though those comments kinda break it. Especially this 212 chars long line
                        pathDeviation = mountainRange[i] - mountainRange[pathStartIndex]
                        pathDeviationStartIndex = i
                    }
                }
            }
        }

        // If the last path was the longest, set it as the longest
        if (isLonger(pathStartIndex, mountainRange.size - 1)) {
            longestPathStartIndex = pathStartIndex
            longestPathEndIndex = mountainRange.size - 1
        }

        return listOf(mountainRange.subList(longestPathStartIndex, longestPathEndIndex + 1))
    }
}
