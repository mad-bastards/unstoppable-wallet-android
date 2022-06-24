package io.horizontalsystems.bankwallet.widgets

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.layout.*
import androidx.glance.text.Text

class MarketWidget : GlanceAppWidget() {

    companion object {
        private val smallMode = DpSize(140.dp, 120.dp)
        private val mediumMode = DpSize(220.dp, 200.dp)
        private val largeMode = DpSize(260.dp, 280.dp)
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(smallMode, mediumMode, largeMode)
    )

    override val stateDefinition = MarketWidgetStateDefinition

    @Composable
    override fun Content() {
        val state = currentState<MarketWidgetState>()

        Log.e("AAA", "Content() state = $state")

//        ComposeAppTheme(darkTheme = true) {
        AppWidgetColumn(
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = state.toString())

            Button(
                "Refresh",
                actionRunCallback<UpdateMarketAction>(
                    actionParametersOf(ActionParameters.Key<Int>("id") to 123)
                )
            )


            Button(
                "Refresh All",
                actionRunCallback<RefreshAllAction>(
                    actionParametersOf(ActionParameters.Key<Int>("id") to 123)
                )
            )


//            when (marketInfo) {
//
//                is MarketInfo.Available -> {
//                    Text(text = marketInfo.data)
//
//                    Button(
//                        "Refresh",
//                        actionRunCallback<UpdateMarketAction>(
//                            actionParametersOf(ActionParameters.Key<Int>("id") to 123)
//                        )
//                    )
//                }
//                MarketInfo.Loading -> {
//                    Text(text = "Loading")
//                }
//                is MarketInfo.Unavailable -> {
//                    Text("Data not available: ${Random.nextInt(1000)} ${marketInfo.message}")
//                    Button("Refresh", actionRunCallback<UpdateMarketAction>())
//                }
//            }
//                App.marketFavoritesManager.getAll().forEach {
//                    Log.e("AAA", it.coinUid)
//                    Text(text = it.coinUid)
//                }
        }
//        }
    }

}

fun GlanceModifier.appWidgetBackgroundCornerRadius(): GlanceModifier {
    if (Build.VERSION.SDK_INT >= 31) {
        cornerRadius(android.R.dimen.system_app_widget_background_radius)
    } else {
        cornerRadius(16.dp)
    }
    return this
}

@Composable
fun AppWidgetColumn(
    modifier: GlanceModifier = GlanceModifier,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = appWidgetBackgroundModifier().then(modifier),
        verticalAlignment = verticalAlignment,
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}

@Composable
fun appWidgetBackgroundModifier() = GlanceModifier
    .fillMaxSize()
    .padding(16.dp)
//    .appWidgetBackground()
//    .background(ComposeAppTheme.colors.lawrence)//?? colors should be moved to another theme class for glance
    .appWidgetBackgroundCornerRadius()


class RefreshAllAction : ActionCallback {
    override suspend fun onRun(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        MarketWorker.enqueue(context = context, refreshAll = true)
    }
}

class UpdateMarketAction : ActionCallback {
    override suspend fun onRun(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {

        // MarketWidget().update(context, glanceId)

//        Log.e("AAA", "actionParams:")

//        parameters.asMap().forEach { (t, u) ->
//            Log.e("AAA", " $t -> $u")
//        }
//        Log.e("AAA", "update action, Thread: ${Thread.currentThread().name}, glanceId: ${glanceId.hashCode()}")
        // Force the worker to refresh

//        val state = getAppWidgetState(context, MarketWidgetStateDefinition, glanceId)
//
//        val intent = Intent(context, MarketWidgetReceiver::class.java)
//
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(glanceId.hashCode()))
//        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
//
//        context.sendBroadcast(intent)

//        MarketWidget().update(context, glanceId)

//        val request = OneTimeWorkRequestBuilder<MarketWorker>().build()
//        WorkManager.getInstance(context).enqueue(request)

        updateAppWidgetState(context, MarketWidgetStateDefinition, glanceId) { state ->
            Log.e("AAA", "updateAction, prev state = $state")
            state.copy(needToRefresh = true)
        }

        MarketWorker.enqueue(context = context)
    }
}