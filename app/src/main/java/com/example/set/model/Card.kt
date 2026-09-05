package com.example.set.model

enum class CardNumber(val value: Int) {
    ONE(1),
    TWO(2),
    THREE(3);

    companion object {
        fun fromValue(v: Int): CardNumber = entries.first { it.value == v }
    }
}

enum class CardShape(val displayName: String) {
    OVAL("Oval"),
    SQUIGGLE("Squiggle"),
    DIAMOND("Diamond")
}

enum class CardShading(val displayName: String) {
    SOLID("Solid"),
    STRIPED("Striped"),
    OPEN("Open")
}

enum class CardColor(val displayName: String) {
    RED("Red"),
    GREEN("Green"),
    PURPLE("Purple")
}

data class Card(
    val id: Int,
    val number: CardNumber,
    val shape: CardShape,
    val shading: CardShading,
    val color: CardColor
) {
    val description: String
        get() = "${number.value} ${color.displayName} ${shading.displayName} ${shape.displayName}(s)"
}
