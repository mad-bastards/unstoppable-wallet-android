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
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.layout.*
import androidx.glance.layout.Alignment.Vertical.Companion.CenterVertically
import androidx.glance.text.*
import io.horizontalsystems.bankwallet.R

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

//        ComposeAppTheme(darkTheme = true) {

        AppWidgetTheme {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .appWidgetBackgroundCornerRadius()
                    .appWidgetBackground()
                    .background(AppWidgetTheme.colors.lawrence)
                    .padding(8.dp),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.Start
            ) {

//                TextStyle(
//                    fontWeight = FontWeight.Medium,
//                    fontSize = 14.sp,
//                    fontStyle = FontStyle.Italic,
//                    letterSpacing = 0.sp,
//                )
                //Text(
                //        text = text,
                //        modifier = modifier,
                //        textAlign = textAlign,
                //        overflow = overflow,
                //        maxLines = maxLines,
                //        onTextLayout = onTextLayout,
                //        style = ComposeAppTheme.typography.subhead1Italic,
                //        color = ComposeAppTheme.colors.jacob,
                //    )

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

//                Text(text = state.toString())

                state.items.forEach {
                    Item(item = it)
                }


//
//                LazyColumn {
//                    items(state.items, { it.title.hashCode().toLong() }) { item ->
//                        Item(item)
//                    }
//                }


//                Image(provider = ImageProvider(Uri.parse(imageUrl)), contentDescription = "test")

                Spacer(modifier = GlanceModifier.height(32.dp))

                Row {
                    Button(
                        "Refresh",
                        actionRunCallback<UpdateMarketAction>()
                    )

                    Button(
                        "Refresh All",
                        actionRunCallback<RefreshAllAction>()
                    )
                }


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

    @Composable
    fun Item(item: MarketItem) {

        Box(
            modifier = GlanceModifier
                .height(60.dp)
//                .clickable(),
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp),
                verticalAlignment = CenterVertically
            ) {
                Image(
                    provider = imageProvider(item.iconLocalPath),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = GlanceModifier
                        .size(24.dp)
                )
                Spacer(modifier = GlanceModifier.width(16.dp))
                Column {
                    MarketCoinFirstRow(coinName = item.title, rate = null)
                    Spacer(modifier = GlanceModifier.height(3.dp))
                    MarketCoinSecondRow(subtitle = item.subtitle, label = item.label)

                }
            }
        }
    }

    @Composable
    fun MarketCoinFirstRow(coinName: String, rate: String?) {
        Row(
            verticalAlignment = CenterVertically
        ) {

            Text(
                modifier = GlanceModifier.defaultWeight(),
                text = coinName,
                maxLines = 1,
                style = TextStyle(AppWidgetTheme.colors.leah, fontSize = 16.sp)
            )

            rate?.let {
                Text(
                    modifier = GlanceModifier.defaultWeight(),
                    text = rate,
                    maxLines = 1,
                    style = TextStyle(color = AppWidgetTheme.colors.leah, fontSize = 16.sp)
                )
            }
        }
    }

    @Composable
    fun MarketCoinSecondRow(
        subtitle: String,
//        marketDataValue: MarketDataValue?,
        label: String?
    ) {
        Row(
            verticalAlignment = CenterVertically
        ) {
            label?.let {
                Badge(text = it)
                Spacer(modifier = GlanceModifier.width(8.dp))
            }

            Text(
                text = subtitle,
                maxLines = 1,
                style = TextStyle(color = AppWidgetTheme.colors.grey, fontSize = 14.sp, fontWeight = FontWeight.Normal)
            )

            Spacer(modifier = GlanceModifier.defaultWeight())
//           marketDataValue?.let {
//                androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
//                MarketDataValueComponent(marketDataValue)
//            }
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
        MarketWorker.enqueue(context = context, refreshAll = true)
    }
}

class UpdateMarketAction : ActionCallback {
    override suspend fun onRun(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, MarketWidgetStateDefinition, glanceId) { state ->
            Log.e("AAA", "updateAction, prev state = $state")
            state.copy(needToRefresh = true)
        }

        MarketWorker.enqueue(context = context)
    }
}