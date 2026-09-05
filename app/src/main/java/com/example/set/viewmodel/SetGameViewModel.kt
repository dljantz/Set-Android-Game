package com.example.set.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.set.logic.SetGameEngine
import com.example.set.model.Card
import com.example.set.model.GameMode
import com.example.set.model.SetValidationResult
import com.example.set.util.SoundAndHaptics
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val boardCards: List<Card> = emptyList(),
    val deck: List<Card> = emptyList(),
    val selectedCards: Set<Card> = emptySet(),
    val matchedCards: Set<Card> = emptySet(),
    val mismatchCards: Set<Card> = emptySet(),
    val hintCards: Set<Card> = emptySet(),
    val hintLevel: Int = 0,
    val score: Int = 0,
    val elapsedSeconds: Long = 0L,
    val isTimerRunning: Boolean = false,
    val gameMode: GameMode = GameMode.CLASSIC,
    val availableSetsCount: Int = 0,
    val bannerMessage: String? = null,
    val bannerIsError: Boolean = false,
    val validationResultForDialog: SetValidationResult? = null,
    val isGameOver: Boolean = false,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val showSetCount: Boolean = true,
    val autoDealIfNoSets: Boolean = true,
    val colorblindMode: Boolean = false,
    val immersiveMode: Boolean = true,
    val showRulesDialog: Boolean = false,
    val showSettingsDialog: Boolean = false
)

class SetGameViewModel(application: Application) : AndroidViewModel(application) {

    private val soundAndHaptics = SoundAndHaptics(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startNewGame(GameMode.CLASSIC)
    }

    fun startNewGame(mode: GameMode = _uiState.value.gameMode) {
        timerJob?.cancel()

        val fullDeck = SetGameEngine.generateDeck().shuffled()
        var currentDeck = fullDeck
        val initialBoard = mutableListOf<Card>()

        // Deal 12 cards
        val (board, remainingDeck) = dealFromDeck(currentDeck, 12)
        initialBoard.addAll(board)
        currentDeck = remainingDeck

        // If auto-deal is on and 0 sets exist, deal 3 more until a set exists or deck is empty
        if (_uiState.value.autoDealIfNoSets) {
            while (!SetGameEngine.hasAnySet(initialBoard) && currentDeck.isNotEmpty()) {
                val (extra, nextDeck) = dealFromDeck(currentDeck, 3)
                initialBoard.addAll(extra)
                currentDeck = nextDeck
            }
        }

        val availableSets = SetGameEngine.findAllSets(initialBoard).size

        _uiState.update {
            it.copy(
                boardCards = initialBoard,
                deck = currentDeck,
                selectedCards = emptySet(),
                matchedCards = emptySet(),
                mismatchCards = emptySet(),
                hintCards = emptySet(),
                hintLevel = 0,
                score = 0,
                elapsedSeconds = 0L,
                isTimerRunning = true,
                gameMode = mode,
                availableSetsCount = availableSets,
                bannerMessage = "Game started! Find your first SET.",
                bannerIsError = false,
                validationResultForDialog = null,
                isGameOver = false
            )
        }

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _uiState.update {
                    if (it.isTimerRunning && !it.isGameOver) {
                        it.copy(elapsedSeconds = it.elapsedSeconds + 1)
                    } else {
                        it
                    }
                }
            }
        }
    }

    fun onCardClicked(card: Card) {
        val state = _uiState.value
        if (state.isGameOver || card in state.matchedCards) return

        if (card in state.selectedCards) {
            // Deselect
            soundAndHaptics.playCardSelect()
            _uiState.update { it.copy(selectedCards = it.selectedCards - card) }
            return
        }

        if (state.selectedCards.size >= 3) {
            return
        }

        val newSelection = state.selectedCards + card
        soundAndHaptics.playCardSelect()

        if (newSelection.size < 3) {
            _uiState.update {
                it.copy(
                    selectedCards = newSelection,
                    hintCards = emptySet(),
                    hintLevel = 0
                )
            }
            return
        }

        // 3 cards selected -> evaluate
        val list = newSelection.toList()
        val c1 = list[0]
        val c2 = list[1]
        val c3 = list[2]

        val validation = SetGameEngine.validateSet(c1, c2, c3)

        if (validation.isSet) {
            handleValidSet(newSelection)
        } else {
            handleInvalidSet(newSelection, validation)
        }
    }

    private fun handleValidSet(selected: Set<Card>) {
        soundAndHaptics.playSetMatch()

        _uiState.update {
            it.copy(
                selectedCards = selected,
                matchedCards = selected,
                hintCards = emptySet(),
                hintLevel = 0,
                score = it.score + 1,
                bannerMessage = "Great! Valid SET found (+1)",
                bannerIsError = false
            )
        }

        viewModelScope.launch {
            delay(400L) // Visual celebration delay

            val currentState = _uiState.value
            val currentBoard = currentState.boardCards.toMutableList()
            var currentDeck = currentState.deck

            // If board > 12, standard rule is NOT to deal replacements, just remove the 3 cards
            if (currentBoard.size > 12) {
                currentBoard.removeAll(selected)
            } else {
                // Board <= 12, replace in-place if deck has cards
                if (currentDeck.isNotEmpty()) {
                    val (newCards, nextDeck) = dealFromDeck(currentDeck, 3)
                    currentDeck = nextDeck

                    // Replace cards at their previous indices to keep layout steady
                    var newCardIdx = 0
                    for (i in currentBoard.indices) {
                        if (currentBoard[i] in selected && newCardIdx < newCards.size) {
                            currentBoard[i] = newCards[newCardIdx++]
                        }
                    }
                    currentBoard.removeAll(selected)
                } else {
                    currentBoard.removeAll(selected)
                }
            }

            // Auto-deal if no sets and setting enabled
            if (currentState.autoDealIfNoSets) {
                while (!SetGameEngine.hasAnySet(currentBoard) && currentDeck.isNotEmpty()) {
                    val (extra, nextDeck) = dealFromDeck(currentDeck, 3)
                    currentBoard.addAll(extra)
                    currentDeck = nextDeck
                }
            }

            val availableSets = SetGameEngine.findAllSets(currentBoard).size
            val isGameOver = currentDeck.isEmpty() && availableSets == 0

            _uiState.update {
                it.copy(
                    boardCards = currentBoard,
                    deck = currentDeck,
                    selectedCards = emptySet(),
                    matchedCards = emptySet(),
                    availableSetsCount = availableSets,
                    isGameOver = isGameOver
                )
            }
        }
    }

    private fun handleInvalidSet(selected: Set<Card>, validation: SetValidationResult) {
        soundAndHaptics.playSetMismatch()

        _uiState.update {
            it.copy(
                selectedCards = selected,
                mismatchCards = selected,
                bannerMessage = validation.summary,
                bannerIsError = true,
                validationResultForDialog = if (it.gameMode == GameMode.PRACTICE) validation else null
            )
        }

        viewModelScope.launch {
            delay(800L)
            _uiState.update {
                it.copy(
                    selectedCards = emptySet(),
                    mismatchCards = emptySet()
                )
            }
        }
    }

    fun onDealMoreCards() {
        val state = _uiState.value
        if (state.deck.isEmpty()) return

        val (newCards, remainingDeck) = dealFromDeck(state.deck, 3)
        val newBoard = state.boardCards + newCards
        val availableSets = SetGameEngine.findAllSets(newBoard).size

        soundAndHaptics.playCardSelect()
        _uiState.update {
            it.copy(
                boardCards = newBoard,
                deck = remainingDeck,
                availableSetsCount = availableSets,
                hintCards = emptySet(),
                hintLevel = 0,
                bannerMessage = "Dealt 3 additional cards."
            )
        }
    }

    fun onHintClicked() {
        val state = _uiState.value
        val sets = SetGameEngine.findAllSets(state.boardCards)

        soundAndHaptics.playHint()

        if (sets.isEmpty()) {
            _uiState.update {
                it.copy(
                    bannerMessage = "No Sets on the board! Tap '+3 Cards' to deal more.",
                    bannerIsError = true
                )
            }
            return
        }

        val firstSet = sets.first()
        val nextLevel = (state.hintLevel % 3) + 1

        when (nextLevel) {
            1 -> {
                _uiState.update {
                    it.copy(
                        hintLevel = 1,
                        hintCards = emptySet(),
                        bannerMessage = "Hint: There are ${sets.size} valid Set(s) on the board.",
                        bannerIsError = false
                    )
                }
            }
            2 -> {
                _uiState.update {
                    it.copy(
                        hintLevel = 2,
                        hintCards = setOf(firstSet.first),
                        bannerMessage = "Hint: One card in a Set has been highlighted.",
                        bannerIsError = false
                    )
                }
            }
            3 -> {
                _uiState.update {
                    it.copy(
                        hintLevel = 3,
                        hintCards = setOf(firstSet.first, firstSet.second),
                        bannerMessage = "Hint: Two cards of a Set highlighted! Find the 3rd!",
                        bannerIsError = false
                    )
                }
            }
        }
    }

    fun onClearSelection() {
        _uiState.update {
            it.copy(
                selectedCards = emptySet(),
                mismatchCards = emptySet(),
                hintCards = emptySet(),
                hintLevel = 0
            )
        }
    }

    fun dismissValidationDialog() {
        _uiState.update { it.copy(validationResultForDialog = null) }
    }

    fun showRulesDialog(show: Boolean) {
        _uiState.update { it.copy(showRulesDialog = show) }
    }

    fun showSettingsDialog(show: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = show) }
    }

    fun toggleSound(enabled: Boolean) {
        soundAndHaptics.soundEnabled = enabled
        _uiState.update { it.copy(soundEnabled = enabled) }
    }

    fun toggleHaptics(enabled: Boolean) {
        soundAndHaptics.hapticsEnabled = enabled
        _uiState.update { it.copy(hapticsEnabled = enabled) }
    }

    fun toggleShowSetCount(enabled: Boolean) {
        _uiState.update { it.copy(showSetCount = enabled) }
    }

    fun toggleAutoDeal(enabled: Boolean) {
        _uiState.update { it.copy(autoDealIfNoSets = enabled) }
    }

    fun toggleColorblind(enabled: Boolean) {
        _uiState.update { it.copy(colorblindMode = enabled) }
    }

    fun toggleImmersiveMode(enabled: Boolean) {
        _uiState.update { it.copy(immersiveMode = enabled) }
    }

    private fun dealFromDeck(deck: List<Card>, count: Int): Pair<List<Card>, List<Card>> {
        val dealCount = count.coerceAtMost(deck.size)
        val dealt = deck.take(dealCount)
        val remaining = deck.drop(dealCount)
        return Pair(dealt, remaining)
    }
}
