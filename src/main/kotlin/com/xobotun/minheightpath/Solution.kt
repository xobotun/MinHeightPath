package com.xobotun.minheightpath

/**
 * Given an array of integers differing no greater than one, find the longest path on the even surface (with deviation differing also no more than by one)
 * E.g. input [3,4,5,6,7,7,7,6,5,5,6], output [6,7,7,7,6]
 *
 * Should be stateless for testing purposes. C'mon, what state can be there?!
 */
interface Solution {
    /**
     * Find the longest even path. Or if there is more than one, list them all for a bonus point.
     */
    fun solve(mountainRange: Iterable<Int>): List<List<Int>>
}
