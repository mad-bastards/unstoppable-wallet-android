package io.horizontalsystems.bankwallet.widgets

import kotlinx.coroutines.delay
import kotlin.random.Random

object MarketRepository {

    suspend fun getMarketData(): String {
        delay(Random.nextInt(1, 3) * 1000L)

        return "MARKET DATA ${Random.nextInt(1000)}"
    }

    suspend fun getMarketItems(): List<MarketItem> {
        return listOf(
            MarketItem(
                "Bitcoin",
                "BTC",
                "1",
                "https://markets.nyc3.digitaloceanspaces.com/coin-icons/bitcoin@3x.png"
            ),
            MarketItem(
                "Ethereum",
                "ETH",
                "2",
                "https://markets.nyc3.digitaloceanspaces.com/coin-icons/ethereum@3x.png"
            ),

            MarketItem(
                "Binance Coin",
                "BNB",
                "5",
                "https://markets.nyc3.digitaloceanspaces.com/coin-icons/binancecoin@3x.png"
            ),
        )
    }

}

