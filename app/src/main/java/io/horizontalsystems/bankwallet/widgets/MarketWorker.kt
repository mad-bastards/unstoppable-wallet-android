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
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.modules.launcher.LauncherActivity
import java.time.Duration


class MarketWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {
        private const val notificationChannelName = "MARKET_WIDGET_CHANNEL_NAME"
        private const val notificationChannelId = "MARKET_WIDGET_CHANNEL_ID"
        private val uniqueWorkName = MarketWorker::class.java.simpleName

        suspend fun enqueue(context: Context, refreshAll: Boolean = false) {
            Log.e("AAA", "MarketWorker enqueue")
            if (refreshAll) {
                GlanceAppWidgetManager(context).getGlanceIds(MarketWidget::class.java).forEach { glanceId ->
                    updateAppWidgetState(context, MarketWidgetStateDefinition, glanceId) {
                        it.copy(needToRefresh = true)
                    }
                }
            }

            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<MarketWorker>(Duration.ofMinutes(15))

            manager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.REPLACE,
                requestBuilder.build()
            )
        }

        fun cancel(context: Context) {
            Log.e("AAA", "MarketWorker cancel")

            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
        }
    }

    override suspend fun doWork(): Result {
        val rate = App.marketKit.coinPrice("bitcoin", "USD")

        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(MarketWidget::class.java)

        Log.e("AAA", "worker doWork()")

        return try {
            glanceIds.forEach { glanceId ->
                var state = getAppWidgetState(context, MarketWidgetStateDefinition, glanceId)

                if (state.needToRefresh) {
                    state = state.copy(needToRefresh = false, loading = true)
                    setWidgetState(glanceId, state)

                    val marketData = MarketRepository.getMarketData()

                    state = state.copy(data = marketData, loading = false)
                    setWidgetState(glanceId, state)

                    var marketItems = MarketRepository.getMarketItems()
                    marketItems = marketItems.map { it.copy(iconLocalPath = getImage(it.iconRemoteUrl)) }

                    state = state.copy(items = marketItems)
                    setWidgetState(glanceId, state)
                }
            }

            Result.success()
        } catch (e: Exception) {
            glanceIds.forEach { glanceId ->
                var state = getAppWidgetState(context, MarketWidgetStateDefinition, glanceId)

                if (state.needToRefresh) {
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
