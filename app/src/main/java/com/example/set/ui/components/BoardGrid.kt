package com.example.set.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isLandscape = maxWidth > maxHeight

        if (isLandscape) {
            // In landscape: 3 rows, dynamic columns (4, 5, 6 for 12, 15, 18 cards)
            val numRows = 3
            val numCols = (cards.size + numRows - 1) / numRows
            val rows = cards.chunked(numCols)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
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
                        repeat(numCols - rowCards.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            // In portrait: 3 columns, dynamic rows (4, 5, 6, 7 rows for 12, 15, 18, 21 cards)
            val numCols = 3
            val rows = cards.chunked(numCols)
            val verticalSpacing = if (rows.size > 4) 4.dp else 6.dp
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(verticalSpacing)
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
                        repeat(numCols - rowCards.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
