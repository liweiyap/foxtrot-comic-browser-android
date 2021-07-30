package com.liweiyap.foxtrot.util

import kotlin.random.Random

/**
 * For detailed explanation of why `Random.nextInt(n)` is both more efficient and less biased than `Math.random() * n`,
 * see: https://stackoverflow.com/a/738651/12367873
 */
object RandomNumberGenerator {

    @JvmStatic fun get(start: Int, end: Int): Int {
        // throws an IllegalArgumentException if condition is not met
        require(start <= end) {
            "RandomNumberGenerator::get(): Start index must be greater than end index"
        }
        return Random(System.nanoTime()).nextInt(start, end + 1)
    }

    @JvmStatic fun get(end: Int): Int {
        // throws an IllegalArgumentException if condition is not met
        require(end >= 0) {
            "RandomNumberGenerator::get(): Start index must be greater than end index"
        }
        return Random(System.nanoTime()).nextInt(end + 1)
    }
}