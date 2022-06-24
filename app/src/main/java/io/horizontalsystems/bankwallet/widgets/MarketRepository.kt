package io.horizontalsystems.bankwallet.widgets

import kotlinx.coroutines.delay
import kotlin.random.Random

object MarketRepository {

    suspend fun getMarketData(): String {
        delay(Random.nextInt(1, 3) * 1000L)

        return "MARKET DATA ${Random.nextInt(1000)}"
    }

}

