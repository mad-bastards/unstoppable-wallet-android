package io.horizontalsystems.bankwallet.widgets

data class MarketWidgetState(
    val widgetId: Int = 0,
    val items: List<MarketItem> = listOf(),
    val loading: Boolean = false,
    val error: String? = null,
    val updateTimestampMillis: Long = System.currentTimeMillis()
) {
    override fun toString(): String {
        return "{ widgetId: $widgetId, loading: $loading, error: $error, items: ${items.joinToString(separator = ", ")} }"
    }
}

data class MarketItem(
    val title: String,
    val subtitle: String,
    val label: String,
    val imageRemoteUrl: String,
    val imageLocalPath: String? = null
) {
    override fun toString(): String {
        return "( title: $title, subtitle: $subtitle, label: $label, imageRemoteUrl: $imageRemoteUrl, imageLocalPath: $imageLocalPath )"
    }
}
