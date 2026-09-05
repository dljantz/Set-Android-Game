package com.example.set.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.set.model.Card
import com.example.set.model.CardColor
import com.example.set.model.CardNumber
import com.example.set.model.CardShading
import com.example.set.model.CardShape
import kotlin.math.roundToInt

@Composable
fun CardView(
    card: Card,
    isSelected: Boolean,
    isHinted: Boolean,
    isMismatch: Boolean,
    isMatched: Boolean,
    isColorblindMode: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Shake animation for mismatch
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(isMismatch) {
        if (isMismatch) {
            repeat(3) {
                shakeOffset.animateTo(12f, tween(40, easing = LinearEasing))
                shakeOffset.animateTo(-12f, tween(40, easing = LinearEasing))
            }
            shakeOffset.animateTo(0f, tween(40, easing = LinearEasing))
        }
    }

    // Scale animation when selected or matched
    val targetScale = when {
        isMatched -> 1.08f
        isSelected -> 1.05f
        else -> 1.0f
    }
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "cardScale"
    )

    // Glowing pulsating border for hinted cards
    val infiniteTransition = rememberInfiniteTransition(label = "hintTransition")
    val hintGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hintGlow"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isMatched -> Color(0xFF2E7D32)
            isMismatch -> Color(0xFFD32F2F)
            isSelected -> Color(0xFF1976D2)
            isHinted -> Color(0xFFFFA000).copy(alpha = hintGlowAlpha)
            else -> Color(0xFFE0E0E0)
        },
        label = "borderColor"
    )

    val borderWidth = when {
        isMatched || isMismatch || isSelected -> 3.dp
        isHinted -> 3.dp
        else -> 1.dp
    }

    val elevation = when {
        isSelected || isMatched -> 8.dp
        else -> 3.dp
    }

    Card(
        modifier = modifier
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .scale(animatedScale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAFAFA)
        ),
        border = BorderStroke(borderWidth, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val symbolColor = resolveCardColor(card.color, isColorblindMode)
                drawCardSymbols(
                    canvasWidth = size.width,
                    canvasHeight = size.height,
                    count = card.number,
                    shape = card.shape,
                    shading = card.shading,
                    color = symbolColor
                )
            }
        }
    }
}

private fun resolveCardColor(color: CardColor, isColorblindMode: Boolean): Color {
    return if (isColorblindMode) {
        when (color) {
            CardColor.RED -> Color(0xFFE65100) // High-contrast Vivid Orange
            CardColor.GREEN -> Color(0xFF00897B) // Distinct Teal
            CardColor.PURPLE -> Color(0xFF3F51B5) // Deep Indigo
        }
    } else {
        when (color) {
            CardColor.RED -> Color(0xFFD32F2F) // Classic Red
            CardColor.GREEN -> Color(0xFF388E3C) // Classic Green
            CardColor.PURPLE -> Color(0xFF7B1FA2) // Classic Purple
        }
    }
}

private fun DrawScope.drawCardSymbols(
    canvasWidth: Float,
    canvasHeight: Float,
    count: CardNumber,
    shape: CardShape,
    shading: CardShading,
    color: Color
) {
    val symbolWidth = canvasWidth * 0.78f
    val symbolHeight = (canvasHeight * 0.22f).coerceAtMost(symbolWidth * 0.46f)
    val symbolLeft = (canvasWidth - symbolWidth) / 2f

    val totalCount = count.value
    // Center the 1, 2, or 3 symbols vertically with balanced spacing
    val yPositions = when (totalCount) {
        1 -> listOf((canvasHeight - symbolHeight) / 2f)
        2 -> {
            val spacing = (canvasHeight * 0.10f).coerceAtMost(symbolHeight * 0.55f)
            val startY = (canvasHeight - (2 * symbolHeight + spacing)) / 2f
            listOf(startY, startY + symbolHeight + spacing)
        }
        3 -> {
            val spacing = (canvasHeight * 0.05f).coerceAtMost(symbolHeight * 0.28f)
            val startY = (canvasHeight - (3 * symbolHeight + 2 * spacing)) / 2f
            listOf(startY, startY + symbolHeight + spacing, startY + 2 * (symbolHeight + spacing))
        }
        else -> emptyList()
    }

    val strokeWidth = 3.dp.toPx()

    for (top in yPositions) {
        val path = createShapePath(shape, symbolLeft, top, symbolWidth, symbolHeight)
        renderShape(path, shape, shading, color, strokeWidth, symbolLeft, top, symbolWidth, symbolHeight)
    }
}

private fun DrawScope.renderShape(
    path: Path,
    shape: CardShape,
    shading: CardShading,
    color: Color,
    strokeWidth: Float,
    left: Float,
    top: Float,
    width: Float,
    height: Float
) {
    when (shading) {
        CardShading.SOLID -> {
            drawPath(path = path, color = color, style = Fill)
        }
        CardShading.OPEN -> {
            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
        CardShading.STRIPED -> {
            // Draw outer border stroke
            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            // Draw clean vertical hatching stripes clipped inside the shape
            clipPath(path) {
                val stripeStep = 5.dp.toPx()
                var x = left + 2f
                while (x <= left + width) {
                    drawLine(
                        color = color,
                        start = Offset(x, top - 2f),
                        end = Offset(x, top + height + 2f),
                        strokeWidth = 1.8.dp.toPx()
                    )
                    x += stripeStep
                }
            }
        }
    }
}

private fun createShapePath(
    shape: CardShape,
    left: Float,
    top: Float,
    width: Float,
    height: Float
): Path {
    return Path().apply {
        when (shape) {
            CardShape.OVAL -> {
                val cornerRadius = height / 2f
                addRoundRect(
                    RoundRect(
                        rect = Rect(left, top, left + width, top + height),
                        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                    )
                )
            }
            CardShape.DIAMOND -> {
                val cx = left + width / 2f
                val cy = top + height / 2f
                moveTo(cx, top)
                lineTo(left + width, cy)
                lineTo(cx, top + height)
                lineTo(left, cy)
                close()
            }
            CardShape.SQUIGGLE -> {
                // Classic Set smooth wavy bean / S-curve path
                val w = width
                val h = height
                moveTo(left + w * 0.16f, top + h * 0.20f)
                // Top dip & curve to the right crest
                cubicTo(
                    left + w * 0.38f, top - h * 0.08f,
                    left + w * 0.68f, top + h * 0.38f,
                    left + w * 0.88f, top + h * 0.14f
                )
                // Right rounded end cap
                cubicTo(
                    left + w * 1.05f, top + h * 0.25f,
                    left + w * 1.02f, top + h * 0.72f,
                    left + w * 0.84f, top + h * 0.80f
                )
                // Bottom curve heading back left
                cubicTo(
                    left + w * 0.62f, top + h * 1.08f,
                    left + w * 0.32f, top + h * 0.62f,
                    left + w * 0.12f, top + h * 0.86f
                )
                // Left rounded end cap
                cubicTo(
                    left - w * 0.05f, top + h * 0.75f,
                    left - w * 0.02f, top + h * 0.28f,
                    left + w * 0.16f, top + h * 0.20f
                )
                close()
            }
        }
    }
}
