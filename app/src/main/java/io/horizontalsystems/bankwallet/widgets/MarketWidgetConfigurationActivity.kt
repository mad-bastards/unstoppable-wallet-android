package io.horizontalsystems.bankwallet.widgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import io.horizontalsystems.core.CoreActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MarketWidgetConfigurationActivity : CoreActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column {
                Text(text = "Select Coin")

                TextButton(onClick = {
                    finishActivity()
                }) {
                    Text(text = "Set BTC")
                }
            }
        }
    }

    private fun finishActivity() {
        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        Log.e("AAA", "configured widget: $appWidgetId")
        val coroutineScope: CoroutineScope = MainScope()
        val context = applicationContext

        coroutineScope.launch {
            val manager = GlanceAppWidgetManager(context)
            for (glanceId in manager.getGlanceIds(MarketWidget::class.java)) {
                val state = getAppWidgetState(context, MarketWidgetStateDefinition, glanceId)

                Log.e("AAA", "state.widgetId = ${state.widgetId}")

                if (state.widgetId == 0 || state.widgetId == appWidgetId) { // initial configuring or reconfiguring
                    updateAppWidgetState(context, MarketWidgetStateDefinition, glanceId) {
                        it.copy(widgetId = appWidgetId)
                    }
//                    MarketWidget().update(context, glanceId) // needed or not?
                    MarketWorker.enqueue(context = context, widgetId = appWidgetId)

                    break
                }
            }
        }

        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

}
