package io.horizontalsystems.bankwallet.widgets

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.layout.*
import androidx.glance.layout.Alignment.Vertical.Companion.CenterVertically
import androidx.glance.text.*
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.modules.launcher.LauncherActivity
import io.horizontalsystems.bankwallet.modules.market.Value
import java.math.BigDecimal

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
        val context = LocalContext.current

        Log.e("AAA", "Content() state = $state")

        AppWidgetTheme {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ImageProvider(R.drawable.widget_background))
//                    .appWidgetBackgroundCornerRadius()
//                    .cornerRadius(28.dp)
//                    .appWidgetBackground()
//                    .background(AppWidgetTheme.colors.lawrence)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = GlanceModifier
                        .background(ImageProvider(R.drawable.widget_list_background))
                ) {
                    Row(
                        modifier = GlanceModifier
                            .height(44.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = CenterVertically
                    ) {
                        Text(
                            modifier = GlanceModifier.fillMaxWidth(),
                            text = context.getString(R.string.Market_Tab_Watchlist),
                            style = TextStyle(
                                color = AppWidgetTheme.colors.grey,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Normal,
                                textAlign = TextAlign.Start
                            )
                        )
                    }

                    state.items.forEach {
                        Box(
                            modifier = GlanceModifier
                                .height(60.dp)
                                .background(ImageProvider(R.drawable.widget_list_item_background))
                                .clickable(
                                    actionStartActivity<LauncherActivity>(
                                        parameters = actionParametersOf(

                                        )
                                    )
                                    /*actionRunCallback<OpenCoinPageAction>(
                                        actionParametersOf(
                                            ActionParameters.Key<String>("coinUid") to it.uid
                                        )
                                    )*/
                                ),
                        ) {
                            Item(item = it)
                        }
                    }
//
//                LazyColumn {
//                    items(state.items, { it.title.hashCode().toLong() }) { item ->
//                        Item(item)
//                    }
//                }

                }

//                Row {
//                    Button(
//                        "Refresh",
//                        actionRunCallback<UpdateMarketAction>()
//                    )
//                    Button(
//                        "Refresh All",
//                        actionRunCallback<RefreshAllAction>()
//                    )
//                }

            }
        }
    }

    @Composable
    fun Item(item: MarketWidgetItem) {
        Row(
            modifier = GlanceModifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp),
            verticalAlignment = CenterVertically
        ) {
            Image(
                provider = imageProvider(item.imageLocalPath),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = GlanceModifier
                    .size(24.dp)
            )
            Spacer(modifier = GlanceModifier.width(16.dp))
            Column {
                MarketCoinFirstRow(coinName = item.title, rate = item.value)
                Spacer(modifier = GlanceModifier.height(3.dp))
                MarketCoinSecondRow(
                    subtitle = item.subtitle,
                    label = item.label,
                    diff = item.diff,
                    marketCap = item.marketCap,
                    volume = item.volume
                )

            }
        }
    }

    @Composable
    fun MarketCoinFirstRow(coinName: String, rate: String?) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = CenterVertically
        ) {

            Text(
                text = coinName,
                maxLines = 1,
                style = TextStyle(AppWidgetTheme.colors.leah, fontSize = 16.sp)
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = rate ?: "",
                maxLines = 1,
                style = TextStyle(color = AppWidgetTheme.colors.leah, fontSize = 16.sp)
            )
        }
    }

    @Composable
    fun MarketCoinSecondRow(
        subtitle: String,
        label: String?,
        diff: BigDecimal?,
        marketCap: String?,
        volume: String?
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = CenterVertically
        ) {
            label?.let {
                Badge(text = it)
                Spacer(modifier = GlanceModifier.width(8.dp))
            }
            Text(
                text = subtitle,
                maxLines = 1,
                style = AppWidgetTheme.textStyles.d1
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            MarketDataValueComponent(diff, marketCap, volume)
        }
    }

    @Composable
    private fun diffColor(value: BigDecimal) =
        if (value.signum() >= 0) {
            AppWidgetTheme.colors.remus
        } else {
            AppWidgetTheme.colors.lucian
        }

    @Composable
    fun MarketDataValueComponent(
        diff: BigDecimal?,
        marketCap: String?,
        volume: String?
    ) {

        when {
            diff != null -> {
                Text(
                    text = App.numberFormatter.formatValueAsDiff(Value.Percent(diff)),
                    style = TextStyle(color = diffColor(diff), fontSize = 14.sp, fontWeight = FontWeight.Normal),
                    maxLines = 1
                )
            }
            marketCap != null -> {
                Row {
                    Text(
                        text = "MCap",
                        style = AppWidgetTheme.textStyles.c3,
                        maxLines = 1
                    )
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = marketCap,
                        style = AppWidgetTheme.textStyles.d1,
                        maxLines = 1
                    )
                }
            }
            volume != null -> {
                Row {
                    Text(
                        text = "Vol",
                        style = AppWidgetTheme.textStyles.d3,
                        maxLines = 1
                    )
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = volume,
                        style = AppWidgetTheme.textStyles.d1,
                        maxLines = 1
                    )
                }
            }
        }
    }

    @Composable
    fun Badge(text: String) {
        Text(
            modifier = GlanceModifier
                .cornerRadius(4.dp)
                .background(AppWidgetTheme.colors.jeremy)
                .padding(horizontal = 4.dp, vertical = 2.dp),
            text = text,
            style = TextStyle(color = AppWidgetTheme.colors.bran, fontSize = 10.sp, fontWeight = FontWeight.Medium),
        )
    }

    private fun imageProvider(path: String?) = if (path == null) {
        ImageProvider(R.drawable.coin_placeholder)
    } else {
        ImageProvider(BitmapFactory.decodeFile(path))
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

//@Composable
//fun AppWidgetColumn(
//    modifier: GlanceModifier = GlanceModifier,
//    verticalAlignment: Alignment.Vertical = Alignment.Top,
//    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
//    content: @Composable ColumnScope.() -> Unit
//) {
//    Column(
//        modifier = appWidgetBackgroundModifier().then(modifier),
//        verticalAlignment = verticalAlignment,
//        horizontalAlignment = horizontalAlignment,
//        content = content,
//    )
//}

//@Composable
//fun appWidgetBackgroundModifier() = GlanceModifier
//    .fillMaxSize()
//    .appWidgetBackgroundCornerRadius()
//    .appWidgetBackground()
//    .background(color = MaterialTheme.colors.background)
//    .padding(8.dp)
//    .fillMaxSize()
//    .padding(16.dp)
//    .appWidgetBackground()
//    .background(ComposeAppTheme.colors.lawrence)//?? colors should be moved to another theme class for glance
//    .appWidgetBackgroundCornerRadius()


class RefreshAllAction : ActionCallback {
    override suspend fun onRun(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        GlanceAppWidgetManager(context).getGlanceIds(MarketWidget::class.java).forEach {
            val state = getAppWidgetState(context, MarketWidgetStateDefinition, it)
            MarketWorker.enqueue(context = context, widgetId = state.widgetId)
        }
    }
}

class UpdateMarketAction : ActionCallback {
    override suspend fun onRun(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val state = getAppWidgetState(context, MarketWidgetStateDefinition, glanceId)
        MarketWorker.enqueue(context = context, widgetId = state.widgetId)
    }
}

class OpenCoinPageAction : ActionCallback {
    override suspend fun onRun(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val coinUid = parameters.get<String>(ActionParameters.Key("coinUid"))
        Log.e("AAA", "coinUid: $coinUid")

        val state = getAppWidgetState(context, MarketWidgetStateDefinition, glanceId)

//        coinUid?.let {
//            val arguments = CoinFragment.prepareParams(coinUid)
//            findNavController().slideFromRight(R.id.coinFragment, arguments)
//        }
//        MarketWorker.enqueue(context = context, widgetId = state.widgetId)
    }
}