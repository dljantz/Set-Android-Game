package com.example.set.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.set.model.Card

@Composable
fun BoardGrid(
    cards: List<Card>,
    selectedCards: Set<Card>,
    hintCards: Set<Card>,
    mismatchCards: Set<Card>,
    matchedCards: Set<Card>,
    isColorblindMode: Boolean,
    onCardClick: (Card) -> Unit,
    modifier: Modifier = Modifier
) {
    if (cards.size <= 12) {
        // Automatically fit all 4 rows on screen without scrolling
        val rows = cards.chunked(3)
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            rows.forEach { rowCards ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rowCards.forEach { card ->
                        CardView(
                            card = card,
                            isSelected = card in selectedCards,
                            isHinted = card in hintCards,
                            isMismatch = card in mismatchCards,
                            isMatched = card in matchedCards,
                            isColorblindMode = isColorblindMode,
                            onClick = { onCardClick(card) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                    // In case row has fewer than 3 cards, fill the remaining slots
                    repeat(3 - rowCards.size) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    } else {
        // If 15+ cards are dealt, use a scrollable grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(
                items = cards,
                key = { it.id }
            ) { card ->
                CardView(
                    card = card,
                    isSelected = card in selectedCards,
                    isHinted = card in hintCards,
                    isMismatch = card in mismatchCards,
                    isMatched = card in matchedCards,
                    isColorblindMode = isColorblindMode,
                    onClick = { onCardClick(card) },
                    modifier = Modifier.aspectRatio(0.76f)
                )
            }
        }
    }
}
