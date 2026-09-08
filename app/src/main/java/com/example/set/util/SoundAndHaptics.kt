package com.example.set.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundAndHaptics(private val context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var soundEnabled: Boolean = false
    var hapticsEnabled: Boolean = true

    fun playCardSelect() {
        if (hapticsEnabled) {
            vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        }
    }

    fun playSetMatch() {
        if (hapticsEnabled) {
            vibrate(VibrationEffect.createWaveform(longArrayOf(0, 40, 50, 60), intArrayOf(0, 180, 0, 255), -1))
        }
    }

    fun playSetMismatch() {
        if (hapticsEnabled) {
            vibrate(VibrationEffect.createWaveform(longArrayOf(0, 80, 40, 80), intArrayOf(0, 220, 0, 160), -1))
        }
    }

    fun playHint() {
        if (hapticsEnabled) {
            vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        }
    }

    private fun vibrate(effect: VibrationEffect) {
        try {
            vibrator?.let {
                if (it.hasVibrator()) {
                    it.vibrate(effect)
                }
            }
        } catch (_: Exception) {}
    }
}
