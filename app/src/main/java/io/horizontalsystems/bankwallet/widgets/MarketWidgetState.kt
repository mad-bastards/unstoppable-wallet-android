package io.horizontalsystems.bankwallet.widgets

data class MarketWidgetState(
    val id: Int = 0,
    val data: String = "",
    val items: List<MarketItem> = listOf(),
    val needToRefresh: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null
) {
    override fun toString(): String {
        return "{ id: $id, data: $data, items: ${items.joinToString(separator = ", ")}, loading: $loading, error: $error }"
    }
}

data class MarketItem(
    val title: String,
    val subtitle: String,
    val label: String,
    val iconRemoteUrl: String,
    val iconLocalPath: String? = null
) {
    override fun toString(): String {
        return "( title: $title, iconRemoteUrl: $iconRemoteUrl, iconLocalPath: $iconLocalPath )"
    }
}
