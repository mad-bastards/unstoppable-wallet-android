package io.horizontalsystems.bankwallet.widgets

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.horizontalsystems.bankwallet.R
import java.math.BigDecimal

data class MarketWidgetState(
    val widgetId: Int = 0,
    val type: MarketWidgetType = MarketWidgetType.Watchlist,
    val items: List<MarketWidgetItem> = listOf(),
    val loading: Boolean = false,
    val error: String? = null,
    val updateTimestampMillis: Long = System.currentTimeMillis()
) {
    override fun toString(): String {
        return "{ widgetId: $widgetId, type: ${type.id}, loading: $loading, error: $error, items: ${items.joinToString(separator = ", ")} }"
    }
}

enum class MarketWidgetType(val title: Int, val id: String) {
    Watchlist(R.string.WidgetList_Type_Watchlist, "watchlist"),
    TopGainers(R.string.WidgetList_Type_TopGainers, "topGainers"),
    TopNfts(R.string.WidgetList_Type_TopNfts, "topNfts"),
    TopPlatforms(R.string.WidgetList_Type_TopPlatforms, "topPlatforms");

    companion object {
        val map = values().associateBy(MarketWidgetType::id)
        fun fromId(id: String): MarketWidgetType? = map[id]
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

    val imageRemoteUrl: String,
    val imageLocalPath: String? = null
) {
    override fun toString(): String {
        return "( title: $title, subtitle: $subtitle, label: $label, value: $value, marketCap: $marketCap, volume: $volume, diff: $diff, imageRemoteUrl: $imageRemoteUrl, imageLocalPath: $imageLocalPath )"
    }
}

class MarketWidgetTypeAdapter : TypeAdapter<MarketWidgetType>() {
    override fun write(writer: JsonWriter, value: MarketWidgetType) {
        writer.value(value.id)
    }

    override fun read(reader: JsonReader): MarketWidgetType? {
        return MarketWidgetType.fromId(reader.nextString())
    }
}
