package com.example.set.logic

import com.example.set.model.Card
import com.example.set.model.CardColor
import com.example.set.model.CardNumber
import com.example.set.model.CardShading
import com.example.set.model.CardShape
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SetGameEngineTest {

    @Test
    fun testGenerateDeck_has81UniqueCards() {
        val deck = SetGameEngine.generateDeck()
        assertEquals(81, deck.size)
        val distinct = deck.distinctBy { listOf(it.number, it.shape, it.shading, it.color) }
        assertEquals(81, distinct.size)
    }

    @Test
    fun testIsSet_allSameAttributes_isValidSet() {
        // All same count, shape, shading, but all different colors
        val c1 = Card(1, CardNumber.ONE, CardShape.OVAL, CardShading.SOLID, CardColor.RED)
        val c2 = Card(2, CardNumber.ONE, CardShape.OVAL, CardShading.SOLID, CardColor.GREEN)
        val c3 = Card(3, CardNumber.ONE, CardShape.OVAL, CardShading.SOLID, CardColor.PURPLE)

        assertTrue(SetGameEngine.isSet(c1, c2, c3))
    }

    @Test
    fun testIsSet_allDifferentAttributes_isValidSet() {
        val c1 = Card(1, CardNumber.ONE, CardShape.OVAL, CardShading.SOLID, CardColor.RED)
        val c2 = Card(2, CardNumber.TWO, CardShape.SQUIGGLE, CardShading.STRIPED, CardColor.GREEN)
        val c3 = Card(3, CardNumber.THREE, CardShape.DIAMOND, CardShading.OPEN, CardColor.PURPLE)

        assertTrue(SetGameEngine.isSet(c1, c2, c3))
    }

    @Test
    fun testIsSet_twoSameOneDifferent_isNotSet() {
        // c1 and c2 are RED, c3 is GREEN -> Invalid!
        val c1 = Card(1, CardNumber.ONE, CardShape.OVAL, CardShading.SOLID, CardColor.RED)
        val c2 = Card(2, CardNumber.TWO, CardShape.OVAL, CardShading.SOLID, CardColor.RED)
        val c3 = Card(3, CardNumber.THREE, CardShape.OVAL, CardShading.SOLID, CardColor.GREEN)

        assertFalse(SetGameEngine.isSet(c1, c2, c3))

        val validation = SetGameEngine.validateSet(c1, c2, c3)
        assertFalse(validation.isSet)
        val colorCheck = validation.checks.first { it.attributeName == "Color" }
        assertFalse(colorCheck.isValid)
    }

    @Test
    fun testFindComplementaryCard_returnsExactThirdCard() {
        val c1 = Card(1, CardNumber.ONE, CardShape.OVAL, CardShading.SOLID, CardColor.RED)
        val c2 = Card(2, CardNumber.TWO, CardShape.OVAL, CardShading.STRIPED, CardColor.GREEN)

        val comp = SetGameEngine.findComplementaryCard(c1, c2)

        assertEquals(CardNumber.THREE, comp.number)
        assertEquals(CardShape.OVAL, comp.shape)
        assertEquals(CardShading.OPEN, comp.shading)
        assertEquals(CardColor.PURPLE, comp.color)

        // Verifying that together they form a valid Set!
        assertTrue(SetGameEngine.isSet(c1, c2, comp))
    }

    @Test
    fun testFindAllSets_findsCorrectSetsInDeck() {
        val cards = listOf(
            Card(1, CardNumber.ONE, CardShape.OVAL, CardShading.SOLID, CardColor.RED),
            Card(2, CardNumber.ONE, CardShape.OVAL, CardShading.SOLID, CardColor.GREEN),
            Card(3, CardNumber.ONE, CardShape.OVAL, CardShading.SOLID, CardColor.PURPLE),
            Card(4, CardNumber.TWO, CardShape.DIAMOND, CardShading.OPEN, CardColor.RED)
        )

        val sets = SetGameEngine.findAllSets(cards)
        assertEquals(1, sets.size)
        assertEquals(cards[0], sets[0].first)
        assertEquals(cards[1], sets[0].second)
        assertEquals(cards[2], sets[0].third)
    }

    @Test
    fun testFullDeck_everyPairHasUniqueValidThirdCard_andTotalSetsIs1080() {
        val deck = SetGameEngine.generateDeck()
        val allSets = SetGameEngine.findAllSets(deck)

        // Combinatorics: 81 * 80 / 6 = 1,080 distinct sets in a standard Set deck
        assertEquals(1080, allSets.size)

        // Check that for any 2 distinct cards in the deck, findComplementaryCard produces a valid card
        for (i in 0 until 20) {
            for (j in (i + 1) until 21) {
                val c1 = deck[i]
                val c2 = deck[j]
                val c3 = SetGameEngine.findComplementaryCard(c1, c2)
                assertTrue(SetGameEngine.isSet(c1, c2, c3))
                assertTrue(deck.any { it.number == c3.number && it.shape == c3.shape && it.shading == c3.shading && it.color == c3.color })
            }
        }
    }

    @Test
    fun testFindAllSets_userReportedCardsScenario_completesProperly() {
        val c1 = Card(1, CardNumber.ONE, CardShape.OVAL, CardShading.OPEN, CardColor.RED)
        val c2 = Card(2, CardNumber.TWO, CardShape.SQUIGGLE, CardShading.STRIPED, CardColor.PURPLE)
        val c3 = Card(3, CardNumber.THREE, CardShape.DIAMOND, CardShading.SOLID, CardColor.GREEN)

        // Verifying that the third card reported by user is indeed the exact mathematical completion
        val complement = SetGameEngine.findComplementaryCard(c1, c2)
        assertEquals(c3.number, complement.number)
        assertEquals(c3.shape, complement.shape)
        assertEquals(c3.shading, complement.shading)
        assertEquals(c3.color, complement.color)
        assertTrue(SetGameEngine.isSet(c1, c2, c3))

        // When c3 is at index 14 (15th card, in the 5th row)
        val dummyCards = (4..15).map { id ->
            Card(id, CardNumber.ONE, CardShape.OVAL, CardShading.SOLID, CardColor.RED)
        }
        val board = listOf(c1, c2) + dummyCards.take(12) + listOf(c3)
        assertEquals(15, board.size)

        val sets = SetGameEngine.findAllSets(board)
        assertTrue(sets.isNotEmpty())
        val foundSet = sets.first { it.first == c1 && it.second == c2 }
        assertEquals(c3, foundSet.third)
        assertTrue(board.contains(foundSet.third))
    }
}
