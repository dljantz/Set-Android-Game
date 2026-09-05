package com.example.set.model

enum class GameMode(val title: String, val description: String) {
    CLASSIC("Classic", "Find as many sets as you can through the 81-card deck with a timer."),
    PRACTICE("Practice & Zen", "Untimed play with detailed explanations for non-sets and unlimited hints."),
    PUZZLE("Set Trainer", "Quick puzzle drills: Identify which 3rd card completes the set!")
}

data class AttributeCheck(
    val attributeName: String,
    val isValid: Boolean,
    val description: String
)

data class SetValidationResult(
    val isSet: Boolean,
    val checks: List<AttributeCheck>,
    val summary: String
)
