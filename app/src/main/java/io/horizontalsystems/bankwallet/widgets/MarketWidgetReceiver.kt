package io.horizontalsystems.bankwallet.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class MarketWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = MarketWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        Log.e("AAA", "receiver: onEnabled")
        // MarketWorker.enqueue(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)

        Log.e("AAA", "receiver: onDisabled")
        // MarketWorker.cancel(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.e("AAA", "receiver: onReceive")
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        Log.e("AAA", "receiver: onAppWidgetOptionsChanged")
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)

        Log.e("AAA", "receiver: onDeleted")
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)

        Log.e("AAA", "receiver: onRestored")
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

//        MarketWidget().update(context,  appWidgetIds.first())
        Log.e("AAA", "receiver: onUpdate, appWidgetIds: ${appWidgetIds.joinToString(separator = ", ")}")
    }
}
