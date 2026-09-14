package com.hydroping.app.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.hydroping.app.domain.ReminderSound
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

object SoundEffectHelper {

    private val audioScope = CoroutineScope(Dispatchers.Default)

    /**
     * Plays the selected reminder sound effect in real time.
     * Uses zero-latency procedural 16-bit PCM audio synthesis.
     */
    fun playSound(context: Context, sound: ReminderSound) {
        if (sound == ReminderSound.SILENT) {
            triggerVibration(context)
            return
        }

        audioScope.launch {
            var audioTrack: AudioTrack? = null
            try {
                val sampleRate = 44100
                val pcmData = when (sound) {
                    ReminderSound.CHIME -> generateChime(sampleRate)
                    ReminderSound.GENTLE_DROP -> generateWaterDrop(sampleRate)
                    ReminderSound.PING -> generateRadarPing(sampleRate)
                    ReminderSound.BUBBLE -> generateBubblePop(sampleRate)
                    ReminderSound.SILENT -> ShortArray(0)
                }

                if (pcmData.isEmpty()) return@launch

                val bufferSize = pcmData.size * 2
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
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
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(pcmData, 0, pcmData.size)
                audioTrack.play()

                val durationMs = (pcmData.size.toFloat() / sampleRate * 1000).toLong() + 80
                delay(durationMs)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (_: Exception) {}
            }
        }
    }

    private fun triggerVibration(context: Context) {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(120)
            }
        } catch (_: Exception) {}
    }

    private fun generateChime(sampleRate: Int): ShortArray {
        val duration = 0.65f
        val numSamples = (duration * sampleRate).toInt()
        val buffer = ShortArray(numSamples)

        val f1 = 1046.5 // C6
        val f2 = 1318.5 // E6
        val f3 = 1567.98 // G6
        val f4 = 2093.0 // C7

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-5.0 * t)
            val sample = (0.35 * sin(2.0 * PI * f1 * t) +
                    0.28 * sin(2.0 * PI * f2 * t) +
                    0.22 * sin(2.0 * PI * f3 * t) +
                    0.15 * sin(2.0 * PI * f4 * t)) * envelope
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateWaterDrop(sampleRate: Int): ShortArray {
        val duration = 0.22f
        val numSamples = (duration * sampleRate).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = t / duration
            val currentFreq = 700.0 + 1200.0 * progress * progress
            val envelope = if (progress < 0.08) progress / 0.08 else exp(-14.0 * (t - 0.08 * duration))
            val sample = sin(2.0 * PI * currentFreq * t) * envelope
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateRadarPing(sampleRate: Int): ShortArray {
        val duration = 0.5f
        val numSamples = (duration * sampleRate).toInt()
        val buffer = ShortArray(numSamples)
        val freq = 1760.0 // A6

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-6.5 * t)
            val harmonic = 0.8 * sin(2.0 * PI * freq * t) + 0.2 * sin(2.0 * PI * (freq * 2.0) * t)
            val sample = harmonic * envelope
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateBubblePop(sampleRate: Int): ShortArray {
        val duration = 0.16f
        val numSamples = (duration * sampleRate).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = t / duration
            val freq = 450.0 + 950.0 * sqrt(progress)
            val envelope = if (progress < 0.12) progress / 0.12 else exp(-18.0 * (t - 0.12 * duration))
            val sample = sin(2.0 * PI * freq * t) * envelope
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }
}
