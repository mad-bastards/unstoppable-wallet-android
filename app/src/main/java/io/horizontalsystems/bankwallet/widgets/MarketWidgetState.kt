package io.horizontalsystems.bankwallet.widgets

data class MarketWidgetState(
    val id: Int = 0,
    val data: String = "",
    val needToRefresh: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null
) {
    override fun toString(): String {
        return "{ id: $id, data: $data, loading: $loading, error: $error }"
    }
}
