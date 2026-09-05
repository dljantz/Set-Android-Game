package com.example.set.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.set.ui.components.BoardGrid
import com.example.set.ui.components.ExplanationDialog
import com.example.set.ui.components.GameOverDialog
import com.example.set.ui.components.GameControls
import com.example.set.ui.components.GameTopBar
import com.example.set.ui.components.RulesDialog
import com.example.set.ui.components.SettingsDialog
import com.example.set.viewmodel.SetGameViewModel

@Composable
fun SetGameScreen(
    viewModel: SetGameViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity

    // Fullscreen / Immersive Mode controller
    DisposableEffect(activity, uiState.immersiveMode) {
        if (activity != null) {
            val window = activity.window
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (uiState.immersiveMode) {
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
        onDispose {
            if (activity != null) {
                val window = activity.window
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    Scaffold(
        topBar = {
            GameTopBar(
                gameMode = uiState.gameMode,
                elapsedSeconds = uiState.elapsedSeconds,
                score = uiState.score,
                deckRemaining = uiState.deck.size,
                availableSetsCount = uiState.availableSetsCount,
                showSetCount = uiState.showSetCount,
                onInfoClick = { viewModel.showRulesDialog(true) },
                onSettingsClick = { viewModel.showSettingsDialog(true) },
                onRestartClick = { viewModel.startNewGame() }
            )
        },
        bottomBar = {
            GameControls(
                deckRemaining = uiState.deck.size,
                availableSetsCount = uiState.availableSetsCount,
                selectedCount = uiState.selectedCards.size,
                bannerMessage = uiState.bannerMessage,
                bannerIsError = uiState.bannerIsError,
                onDealMoreClick = { viewModel.onDealMoreCards() },
                onHintClick = { viewModel.onHintClicked() },
                onClearSelectionClick = { viewModel.onClearSelection() }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            BoardGrid(
                cards = uiState.boardCards,
                selectedCards = uiState.selectedCards,
                hintCards = uiState.hintCards,
                mismatchCards = uiState.mismatchCards,
                matchedCards = uiState.matchedCards,
                isColorblindMode = uiState.colorblindMode,
                onCardClick = { card -> viewModel.onCardClicked(card) }
            )
        }
    }

    // Dialogs
    if (uiState.showRulesDialog) {
        RulesDialog(onDismiss = { viewModel.showRulesDialog(false) })
    }

    if (uiState.showSettingsDialog) {
        SettingsDialog(
            gameMode = uiState.gameMode,
            soundEnabled = uiState.soundEnabled,
            hapticsEnabled = uiState.hapticsEnabled,
            showSetCount = uiState.showSetCount,
            autoDealIfNoSets = uiState.autoDealIfNoSets,
            colorblindMode = uiState.colorblindMode,
            immersiveMode = uiState.immersiveMode,
            onModeSelected = { mode ->
                viewModel.startNewGame(mode)
                viewModel.showSettingsDialog(false)
            },
            onSoundToggled = { viewModel.toggleSound(it) },
            onHapticsToggled = { viewModel.toggleHaptics(it) },
            onShowSetCountToggled = { viewModel.toggleShowSetCount(it) },
            onAutoDealToggled = { viewModel.toggleAutoDeal(it) },
            onColorblindToggled = { viewModel.toggleColorblind(it) },
            onImmersiveModeToggled = { viewModel.toggleImmersiveMode(it) },
            onDismiss = { viewModel.showSettingsDialog(false) }
        )
    }

    if (uiState.isGameOver) {
        GameOverDialog(
            score = uiState.score,
            elapsedSeconds = uiState.elapsedSeconds,
            onPlayAgain = { viewModel.startNewGame() },
            onDismiss = { /* Player can dismiss to review the final board */ }
        )
    }

    uiState.validationResultForDialog?.let { result ->
        ExplanationDialog(
            validationResult = result,
            onDismiss = { viewModel.dismissValidationDialog() }
        )
    }
}
