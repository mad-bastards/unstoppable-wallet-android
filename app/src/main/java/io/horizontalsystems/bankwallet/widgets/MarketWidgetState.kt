package io.horizontalsystems.bankwallet.widgets

import java.math.BigDecimal

data class MarketWidgetState(
    val widgetId: Int = 0,
    val items: List<MarketWidgetItem> = listOf(),
    val loading: Boolean = false,
    val error: String? = null,
    val updateTimestampMillis: Long = System.currentTimeMillis()
) {
    override fun toString(): String {
        return "{ widgetId: $widgetId, loading: $loading, error: $error, items: ${items.joinToString(separator = ", ")} }"
    }
}

data class MarketWidgetItem(
    val uid: String,
    val title: String,
    val subtitle: String,
    val label: String,

    val value: String,
    val marketCap: String?,
    val volume: String?,
    val diff: BigDecimal?,

//    val marketDataValue: MarketDataValue,

    val imageRemoteUrl: String,
    val imageLocalPath: String? = null
) {
    override fun toString(): String {
        return "( title: $title, subtitle: $subtitle, label: $label, value: $value, marketCap: $marketCap, volume: $volume, diff: $diff, imageRemoteUrl: $imageRemoteUrl, imageLocalPath: $imageLocalPath )"
    }
}
