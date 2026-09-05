package com.example.set.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundAndHaptics(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true

    fun playCardSelect() {
        if (hapticsEnabled) {
            vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        }
        if (soundEnabled) {
            playTone(800.0, 35, 0.25f)
        }
    }

    fun playSetMatch() {
        if (hapticsEnabled) {
            vibrate(VibrationEffect.createWaveform(longArrayOf(0, 40, 50, 60), intArrayOf(0, 180, 0, 255), -1))
        }
        if (soundEnabled) {
            // Play a pleasant rapid major chord arpeggio: C5, E5, G5, C6
            scope.launch {
                playTone(523.25, 60, 0.4f)
                kotlinx.coroutines.delay(50)
                playTone(659.25, 60, 0.45f)
                kotlinx.coroutines.delay(50)
                playTone(783.99, 70, 0.5f)
                kotlinx.coroutines.delay(60)
                playTone(1046.50, 140, 0.55f)
            }
        }
    }

    fun playSetMismatch() {
        if (hapticsEnabled) {
            vibrate(VibrationEffect.createWaveform(longArrayOf(0, 80, 40, 80), intArrayOf(0, 220, 0, 160), -1))
        }
        if (soundEnabled) {
            scope.launch {
                playTone(220.0, 70, 0.35f)
                kotlinx.coroutines.delay(60)
                playTone(180.0, 100, 0.35f)
            }
        }
    }

    fun playHint() {
        if (hapticsEnabled) {
            vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        }
        if (soundEnabled) {
            scope.launch {
                playTone(880.0, 50, 0.3f)
                kotlinx.coroutines.delay(50)
                playTone(1174.66, 90, 0.35f)
            }
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

    private fun playTone(frequencyHz: Double, durationMs: Int, volume: Float) {
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val samples = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    // Apply smooth envelope to prevent clicking at start/end
                    val envelope = when {
                        i < numSamples * 0.1 -> i / (numSamples * 0.1)
                        i > numSamples * 0.7 -> (numSamples - i) / (numSamples * 0.3)
                        else -> 1.0
                    }
                    val sampleValue = (sin(2.0 * PI * frequencyHz * time) * Short.MAX_VALUE * volume * envelope).toInt()
                    samples[i] = sampleValue.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(samples.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(samples, 0, samples.size)
                audioTrack.play()
                // Release audioTrack after playback finishes
                kotlinx.coroutines.delay(durationMs.toLong() + 50)
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }
}
