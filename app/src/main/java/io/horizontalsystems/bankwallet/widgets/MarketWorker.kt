package io.horizontalsystems.bankwallet.widgets

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.*
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.modules.launcher.LauncherActivity
import java.time.Duration


class MarketWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {
        private const val updatePeriodMillis: Long = 15 * 60 * 1000 // 15 minutes
        private const val inputDataKeyWidgetId = "widgetIdKey"
        private const val notificationChannelName = "MARKET_WIDGET_CHANNEL_NAME"
        private const val notificationChannelId = "MARKET_WIDGET_CHANNEL_ID"

        private fun uniqueWorkName(widgetId: Int) = "${MarketWorker::class.java.simpleName}_${widgetId}"

        fun enqueue(context: Context, widgetId: Int) {
            Log.e("AAA", "worker #${widgetId} enqueue()")

            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<MarketWorker>(Duration.ofMillis(updatePeriodMillis))

            val inputData = Data.Builder().putInt(inputDataKeyWidgetId, widgetId).build()
            requestBuilder.setInputData(inputData)

            manager.enqueueUniquePeriodicWork(
                uniqueWorkName(widgetId),
                ExistingPeriodicWorkPolicy.REPLACE,
                requestBuilder.build()
            )
        }

        fun cancel(context: Context, widgetId: Int) {
            Log.e("AAA", "worker #$widgetId cancel()")

            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName(widgetId))
        }
    }

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(MarketWidget::class.java)
        val currentTimestampMillis = System.currentTimeMillis()

        val widgetId = inputData.getInt(inputDataKeyWidgetId, 0)

        Log.e("AAA", "worker #$widgetId doWork(), currentTimestamp seconds = ${currentTimestampMillis / 1000}")

        return try {
            for (glanceId in glanceIds) {
                var state = getAppWidgetState(context, MarketWidgetStateDefinition, glanceId)
                if (state.widgetId != widgetId) continue

                state = state.copy(loading = true, updateTimestampMillis = currentTimestampMillis)
                setWidgetState(glanceId, state)

                val imagePathCache = buildMap {
                    state.items.forEach { item ->
                        item.imageLocalPath?.let { set(item.imageRemoteUrl, it) }
                    }
                }
                var marketItems = MarketRepository.getMarketItems()
                marketItems = marketItems.map { it.copy(imageLocalPath = imagePathCache[it.imageRemoteUrl]) }

                state = state.copy(items = marketItems, loading = false)
                setWidgetState(glanceId, state)

                marketItems = marketItems.map { item ->
                    Log.e("AAA", if (item.imageLocalPath != null) "icon EXISTS" else "NO ICON")

                    item.copy(imageLocalPath = item.imageLocalPath ?: getImage(item.imageRemoteUrl))
                }

                state = state.copy(items = marketItems)
                setWidgetState(glanceId, state)

                break
            }

            Result.success()
        } catch (e: Exception) {
            glanceIds.forEach { glanceId ->
                var state = getAppWidgetState(context, MarketWidgetStateDefinition, glanceId)

                if (state.widgetId == widgetId) {
                    state = state.copy(loading = false, error = e.message ?: e.javaClass.simpleName)
                    setWidgetState(glanceId, state)
                }
            }
            if (runAttemptCount < 10) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    private suspend fun getImage(url: String): String? {
        Log.e("AAA", "getImage: $url")

        val request = ImageRequest.Builder(context)
            .data(url)
            .build()

        with(context.imageLoader) {
            val result = execute(request)
            if (result is ErrorResult) {
                return null
            }
        }

        val localPath = context.imageLoader.diskCache?.get(url)?.use { snapshot ->
            snapshot.data.toFile().path
        }

        return localPath
    }

    private suspend fun setWidgetState(glanceId: GlanceId, state: MarketWidgetState) {
        updateAppWidgetState(context, MarketWidgetStateDefinition, glanceId) {
            state
        }
        MarketWidget().update(context, glanceId)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            notificationChannelId,
            notificationChannelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, notificationChannelId)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, LauncherActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setSmallIcon(R.drawable.ic_refresh)
            .setOngoing(true)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentTitle(context.getString(R.string.app_name))
            .setLocalOnly(true)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setContentText("Updating widget")
            .build()

        return ForegroundInfo(1234, notification)
    }

}
