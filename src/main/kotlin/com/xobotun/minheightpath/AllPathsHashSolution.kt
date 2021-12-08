package com.xobotun.minheightpath

/**
 * Pathfinder that returns all longest paths.
 * Fast and easy to write, yet takes O(n) of additional memory and has O(n^2) time complexity.
 */
class AllPathsHashSolution: Solution {

    override fun solve(mountainRange: List<Int>): List<Path> {
        val pathLengthMap: PathLengthMap = mutableMapOf()

        findAllPaths(mountainRange, pathLengthMap)

        val longestPath = pathLengthMap.keys.maxOrNull() ?: return listOf(emptyList())
        return pathLengthMap[longestPath]!!
    }

    private fun findAllPaths(mountainRange: List<Int>, pathLengthMap: PathLengthMap) {
        if (mountainRange.isEmpty()) return

        for (index in 0..mountainRange.size - 1) {
            for (pathLength in 1..mountainRange.size - index) {
                if (isValidPath(mountainRange, index, index + pathLength - 1)) pathLengthMap.add(mountainRange.subList(index, index + pathLength)) else break
            }
        }
    }

    private fun isValidPath(mountainRange: List<Int>, fromIndex: Int, toIndex: Int): Boolean {
        val initialValue = mountainRange[fromIndex]
        var allowableValue: Int? = null
        for (index in fromIndex + 1..toIndex) {
            val currentValue = mountainRange[index]
            if (currentValue != initialValue && allowableValue == null) allowableValue = currentValue
            if (currentValue != initialValue && currentValue != allowableValue) return false
        }

        return true
    }

    private fun PathLengthMap.add(path: Path) {
        val pathLength = path.size

        if (!containsKey(pathLength)) this[pathLength] = mutableListOf()

        this[pathLength]!!.add(path)
    }
}

private typealias PathLengthMap = MutableMap<Int, MutableList<Path>>
