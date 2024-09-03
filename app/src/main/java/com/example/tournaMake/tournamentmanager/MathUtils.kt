package com.example.tournaMake.tournamentmanager

/**
 * See https://stackoverflow.com/questions/600293/how-to-check-if-a-number-is-a-power-of-2
 * */
fun isPowerOf2(n: Int) : Boolean {
    return n > 0 && (n and (n - 1)) == 0
}