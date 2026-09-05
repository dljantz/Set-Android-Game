package com.example.set.logic

import com.example.set.model.AttributeCheck
import com.example.set.model.Card
import com.example.set.model.CardColor
import com.example.set.model.CardNumber
import com.example.set.model.CardShading
import com.example.set.model.CardShape
import com.example.set.model.SetValidationResult

object SetGameEngine {

    /**
     * Generates all 81 unique cards in the Set deck.
     */
    fun generateDeck(): List<Card> {
        val deck = ArrayList<Card>(81)
        var id = 0
        for (num in CardNumber.entries) {
            for (shape in CardShape.entries) {
                for (shading in CardShading.entries) {
                    for (color in CardColor.entries) {
                        deck.add(Card(id++, num, shape, shading, color))
                    }
                }
            }
        }
        return deck
    }

    /**
     * Checks whether 3 cards form a valid Set.
     */
    fun isSet(c1: Card, c2: Card, c3: Card): Boolean {
        return checkAttribute(c1.number, c2.number, c3.number) &&
                checkAttribute(c1.shape, c2.shape, c3.shape) &&
                checkAttribute(c1.shading, c2.shading, c3.shading) &&
                checkAttribute(c1.color, c2.color, c3.color)
    }

    private fun <T> checkAttribute(a: T, b: T, c: T): Boolean {
        val allSame = (a == b && b == c)
        val allDiff = (a != b && b != c && a != c)
        return allSame || allDiff
    }

    /**
     * Provides an attribute-by-attribute breakdown of whether 3 cards form a Set,
     * explaining which attributes matched or mismatched.
     */
    fun validateSet(c1: Card, c2: Card, c3: Card): SetValidationResult {
        val numberCheck = evaluateAttribute(
            "Number",
            listOf(c1.number.value.toString(), c2.number.value.toString(), c3.number.value.toString()),
            checkAttribute(c1.number, c2.number, c3.number)
        )
        val shapeCheck = evaluateAttribute(
            "Shape",
            listOf(c1.shape.displayName, c2.shape.displayName, c3.shape.displayName),
            checkAttribute(c1.shape, c2.shape, c3.shape)
        )
        val shadingCheck = evaluateAttribute(
            "Shading",
            listOf(c1.shading.displayName, c2.shading.displayName, c3.shading.displayName),
            checkAttribute(c1.shading, c2.shading, c3.shading)
        )
        val colorCheck = evaluateAttribute(
            "Color",
            listOf(c1.color.displayName, c2.color.displayName, c3.color.displayName),
            checkAttribute(c1.color, c2.color, c3.color)
        )

        val checks = listOf(numberCheck, shapeCheck, shadingCheck, colorCheck)
        val isSet = checks.all { it.isValid }
        val summary = if (isSet) {
            "Valid Set! All attributes are all same or all different."
        } else {
            val failed = checks.filter { !it.isValid }.map { it.attributeName }
            "Not a Set: Mismatch in ${failed.joinToString(", ")}."
        }

        return SetValidationResult(isSet = isSet, checks = checks, summary = summary)
    }

    private fun evaluateAttribute(name: String, values: List<String>, isValid: Boolean): AttributeCheck {
        val desc = if (values[0] == values[1] && values[1] == values[2]) {
            "All same (${values[0]})"
        } else if (values.distinct().size == 3) {
            "All different (${values.joinToString(", ")})"
        } else {
            "Mismatch: 2 of one, 1 of another (${values.joinToString(", ")})"
        }
        return AttributeCheck(name, isValid, desc)
    }

    /**
     * Given any 2 cards, computes the exact 3rd card in the 81-card universe
     * that completes a valid Set.
     */
    fun findComplementaryCard(c1: Card, c2: Card): Card {
        val num = completeAttribute(c1.number, c2.number, CardNumber.entries)
        val shape = completeAttribute(c1.shape, c2.shape, CardShape.entries)
        val shading = completeAttribute(c1.shading, c2.shading, CardShading.entries)
        val color = completeAttribute(c1.color, c2.color, CardColor.entries)

        // Compute ID matching the generator formula:
        val id = num.ordinal * 27 + shape.ordinal * 9 + shading.ordinal * 3 + color.ordinal
        return Card(id, num, shape, shading, color)
    }

    private fun <T> completeAttribute(v1: T, v2: T, allValues: List<T>): T {
        return if (v1 == v2) {
            v1
        } else {
            allValues.first { it != v1 && it != v2 }
        }
    }

    /**
     * Finds all valid Sets among the given collection of cards.
     */
    fun findAllSets(cards: List<Card>): List<Triple<Card, Card, Card>> {
        val result = mutableListOf<Triple<Card, Card, Card>>()
        val size = cards.size
        for (i in 0 until size - 2) {
            val c1 = cards[i]
            for (j in i + 1 until size - 1) {
                val c2 = cards[j]
                for (k in j + 1 until size) {
                    val c3 = cards[k]
                    if (isSet(c1, c2, c3)) {
                        result.add(Triple(c1, c2, c3))
                    }
                }
            }
        }
        return result
    }

    /**
     * Checks if at least one Set exists on the board.
     */
    fun hasAnySet(cards: List<Card>): Boolean {
        val size = cards.size
        for (i in 0 until size - 2) {
            val c1 = cards[i]
            for (j in i + 1 until size - 1) {
                val c2 = cards[j]
                for (k in j + 1 until size) {
                    if (isSet(c1, c2, cards[k])) return true
                }
            }
        }
        return false
    }
}
