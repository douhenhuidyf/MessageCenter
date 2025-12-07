package com.example.messagecenter.utils


import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

import com.example.messagecenter.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun timestampToString(timestamp: Long): String {
    val nowTime = System.currentTimeMillis()
    val difference = nowTime - timestamp
    var timeString = ""
    val oneMinute = 60 * 1000
    val oneHour = 60 * oneMinute
    val oneDay = 24 * oneHour
    val yesterday = 2 * oneDay
    val sevenDays = 7 * oneDay
    when{
        difference < oneMinute -> timeString = stringResource(R.string.just_now)
        difference < oneHour -> timeString = "${difference / (60 * 1000)}${stringResource(R.string.minutes_ago)}"
        difference < oneDay -> timeString = "${difference / (60 * 60 * 1000)}${stringResource(R.string.hours_ago)}"
        difference < yesterday -> timeString = stringResource(R.string.yesterday)
        difference < sevenDays -> timeString = "${difference / oneDay}${stringResource(R.string.days_ago)}"
        else -> {
            val date = java.util.Date(timestamp)
            val dateFormat = SimpleDateFormat("YY-MM-dd")
            timeString = dateFormat.format(date)
        }
    }
    return timeString
}

fun getFirstKeywordIndices(allText: String, keyword: String): List<Int> {
    val indexList = mutableListOf<Int>()
    if (allText.isBlank() || keyword.isBlank()) return indexList

    val pattern = Pattern.compile(keyword, Pattern.LITERAL)
    val matcher: Matcher = pattern.matcher(allText)

    if (matcher.find()) {
        indexList.add(matcher.start())
        indexList.add(matcher.end())
    }
    return indexList
}

class NetworkConnectivityObserver(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observe(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }
            override fun onLost(network: Network) {
                trySend(false)
            }
            override fun onUnavailable() {
                trySend(false)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)
        trySend(isConnected(connectivityManager))
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    private fun isConnected(manager: ConnectivityManager): Boolean {
        val network = manager.activeNetwork ?: return false
        val activeNetwork = manager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}