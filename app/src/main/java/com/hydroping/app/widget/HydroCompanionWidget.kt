package com.hydroping.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.hydroping.app.R
import com.hydroping.app.data.HydrationDatabase
import com.hydroping.app.data.UserPreferencesRepository
import com.hydroping.app.domain.CharacterCatalog
import com.hydroping.app.domain.CharacterMood
import com.hydroping.app.domain.CharacterStateMachine
import com.hydroping.app.domain.DialogueEngine
import com.hydroping.app.domain.PersonalityType
import kotlinx.coroutines.flow.first
import java.util.Calendar

class HydroCompanionWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = HydrationDatabase.getDatabase(context)
        val prefs = UserPreferencesRepository(context)

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = cal.timeInMillis
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000)

        val todayTotal = db.drinkDao().getTodayTotalMl(startOfDay, endOfDay)
        val lastDrink = db.drinkDao().getLastDrink()
        val targetGoal = prefs.dailyGoalFlow.first()
        val favAmount = prefs.favoriteAmountFlow.first()
        val characterId = prefs.characterIdFlow.first()
        val personalityStr = prefs.personalityModeFlow.first()
        val activeHours = prefs.activeHoursFlow.first()

        val personality = try {
            PersonalityType.valueOf(personalityStr)
        } catch (_: Exception) {
            PersonalityType.CUTE
        }

        val stateMachine = CharacterStateMachine()
        val mood = stateMachine.determineMood(
            lastDrinkTimeMs = lastDrink?.timestampMs,
            currentTotalMl = todayTotal,
            targetDailyMl = targetGoal,
            quietStartHour = activeHours.second,
            quietEndHour = activeHours.first
        )

        val progressPercent = if (targetGoal > 0) ((todayTotal.toFloat() / targetGoal) * 100).toInt() else 0
        val remainingMl = kotlin.math.max(0, targetGoal - todayTotal)
        val dialogue = DialogueEngine.getDialogue(
            characterId = characterId,
            personality = personality,
            mood = mood,
            percentProgress = progressPercent,
            isBehindTarget = false,
            deficitMl = 0,
            remainingMl = remainingMl
        )
        val character = CharacterCatalog.fromId(characterId)
        val mascotRes = CharacterCatalog.getDrawable(characterId, mood)

        provideContent {
            GlanceTheme {
                WidgetContent(
                    mascotRes = mascotRes,
                    dialogue = dialogue,
                    todayTotal = todayTotal,
                    targetGoal = targetGoal,
                    progressPercent = progressPercent,
                    favAmount = favAmount
                )
            }
        }
    }

    @Composable
    private fun WidgetContent(
        mascotRes: Int,
        dialogue: String,
        todayTotal: Int,
        targetGoal: Int,
        progressPercent: Int,
        favAmount: Int
    ) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xFF0F172A)))
                .cornerRadius(24.dp)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Character Image
                Image(
                    provider = ImageProvider(mascotRes),
                    contentDescription = "Companion Avatar",
                    modifier = GlanceModifier
                        .size(72.dp)
                        .cornerRadius(16.dp)
                )

                Spacer(modifier = GlanceModifier.width(10.dp))

                // Info & Action Column
                Column(
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    Text(
                        text = dialogue,
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 2
                    )

                    Spacer(modifier = GlanceModifier.height(4.dp))

                    Text(
                        text = "$todayTotal / $targetGoal ml ($progressPercent%)",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF38BDF8)),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            text = "+$favAmount ml",
                            onClick = actionRunCallback<WidgetDrinkActionCallback>(
                                actionParametersOf(WidgetDrinkActionCallback.KEY_AMOUNT_ML to favAmount)
                            ),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = ColorProvider(Color(0xFF0284C7)),
                                contentColor = ColorProvider(Color.White)
                            ),
                            modifier = GlanceModifier.height(34.dp)
                        )

                        Spacer(modifier = GlanceModifier.width(6.dp))

                        Button(
                            text = "+500 ml",
                            onClick = actionRunCallback<WidgetDrinkActionCallback>(
                                actionParametersOf(WidgetDrinkActionCallback.KEY_AMOUNT_ML to 500)
                            ),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = ColorProvider(Color(0xFF1E293B)),
                                contentColor = ColorProvider(Color(0xFF38BDF8))
                            ),
                            modifier = GlanceModifier.height(34.dp)
                        )
                    }
                }
            }
        }
    }
}

class HydroWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HydroCompanionWidget()
}
