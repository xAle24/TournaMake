package com.example.tournaMake.mylibrary.displaymodels

import com.example.tournaMake.mylibrary.displaymodels.BracketRoundDisplayModel

/**
 * Represents an entire bracket in a tournament. For a single elimination tournament, there should be only
 * one entity in our [rounds] list. For a double (or more) elim tournament, we'll have multiple.
 */
data class BracketDisplayModel(
    val name: String,
    val rounds: List<BracketRoundDisplayModel>,
)
