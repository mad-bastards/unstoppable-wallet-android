package io.horizontalsystems.bankwallet.entities

import android.os.Parcelable
import io.horizontalsystems.bankwallet.modules.transactions.TransactionSource
import io.horizontalsystems.marketkit.models.CoinType
import io.horizontalsystems.marketkit.models.PlatformCoin
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Wallet(
    val configuredPlatformCoin: ConfiguredPlatformCoin,
    val account: Account
) : Parcelable {
    private val blockchain: TransactionSource.Blockchain
        get() = when (val coinType = coinType) {
            CoinType.Bitcoin -> TransactionSource.Blockchain.Bitcoin
            CoinType.BitcoinCash -> TransactionSource.Blockchain.BitcoinCash
            CoinType.Dash -> TransactionSource.Blockchain.Dash
            CoinType.Litecoin -> TransactionSource.Blockchain.Litecoin
            CoinType.Ethereum -> TransactionSource.Blockchain.Ethereum
            CoinType.BinanceSmartChain -> TransactionSource.Blockchain.BinanceSmartChain
            CoinType.Zcash -> TransactionSource.Blockchain.Zcash
            is CoinType.Bep2 -> TransactionSource.Blockchain.Bep2(coinType.symbol)
            is CoinType.Erc20 -> TransactionSource.Blockchain.Ethereum
            is CoinType.Bep20 -> TransactionSource.Blockchain.BinanceSmartChain
            is CoinType.Sol20, is CoinType.Unsupported -> throw IllegalArgumentException("Unsupported coin may not have transactions to show")
        }

    val platformCoin
        get() = configuredPlatformCoin.platformCoin

    val coinSettings
        get() = configuredPlatformCoin.coinSettings

    val coin
        get() = platformCoin.coin

    val platform
        get() = platformCoin.platform

    val coinType
        get() = platformCoin.coinType

    val decimal
        get() = platform.decimal

    val transactionSource get() = TransactionSource(blockchain, account, coinSettings)

    constructor(platformCoin: PlatformCoin, account: Account) : this(ConfiguredPlatformCoin(platformCoin), account)

    override fun equals(other: Any?): Boolean {
        if (other is Wallet) {
            return platformCoin == other.platformCoin && account == other.account
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return Objects.hash(platformCoin, account)
    }
}
