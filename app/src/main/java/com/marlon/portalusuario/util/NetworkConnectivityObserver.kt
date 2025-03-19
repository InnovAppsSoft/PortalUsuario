package com.marlon.portalusuario.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.core.content.ContextCompat
import com.marlon.portalusuario.trafficbubble.FloatingBubbleService

/**
 * NetworkConnectivityObserver
 *
 * Observes network connectivity changes and provides callbacks for network availability and loss.
 * Optionally, it can manage a floating bubble service based on network status.
 *
 * @param context The application context.
 * @param showTrafficBubble Boolean indicating whether to show the floating bubble service when network is available.
 */
class NetworkConnectivityObserver(
    private val context: Context,
    private val showTrafficBubble: Boolean
) {

    companion object {
        private const val TAG = "NetworkObserver"
        private const val NETWORK_TYPE_EXTRA = "networkType"
    }

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var isNetworkAvailable = false

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d(TAG, "Network is available: $network")
            isNetworkAvailable = true
            handleNetworkAvailability()
        }

        override fun onLost(network: Network) {
            Log.d(TAG, "Network is lost: $network")
            isNetworkAvailable = false
            handleNetworkLoss()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            Log.d(TAG, "Network capabilities changed: $networkCapabilities")
            if (isNetworkAvailable) {
                handleNetworkAvailability()
            }
        }
    }

    /**
     * Starts monitoring the network connectivity status.
     *
     * This function registers a network callback with the ConnectivityManager to listen for changes
     * in network availability and capabilities, specifically looking for internet connectivity.
     *
     * The callback, `networkCallback`, which must be defined elsewhere, will be invoked when:
     *   - A network satisfying the specified criteria becomes available.
     *   - A network satisfying the specified criteria becomes unavailable.
     *   - The capabilities of a network satisfying the specified criteria change.
     *
     * The function builds a `NetworkRequest` specifying that the application is interested in networks
     * that have the `NET_CAPABILITY_INTERNET` capability, indicating the ability to access the internet.
     * It then registers this request with the `ConnectivityManager` along with the provided
     * `networkCallback`.
     *
     * **Important Considerations:**
     * - The `networkCallback` must be defined and implemented before calling this function.
     * - The callback will continue to receive updates until it is unregistered using
     *   `connectivityManager.unregisterNetworkCallback(networkCallback)`. It is crucial to
     *   unregister the callback when it's no longer needed to avoid memory leaks.
     * - This method requires the `ACCESS_NETWORK_STATE` permission in the AndroidManifest.xml.
     *
     * @see ConnectivityManager
     * @see NetworkRequest
     * @see NetworkCapabilities
     * @see ConnectivityManager.registerNetworkCallback
     * @see ConnectivityManager.unregisterNetworkCallback
     */
    fun startMonitoring() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    /**
     * Stops monitoring network connectivity changes.
     *
     * This function unregisters the network callback that was previously registered
     * to listen for network connectivity changes. After calling this function,
     * the application will no longer receive updates about changes in the network
     * state.
     *
     * It's crucial to call this function when the component responsible for
     * monitoring network changes is no longer active or needed (e.g., in the
     * `onDestroy` lifecycle method of an Activity or Fragment). Failing to do so
     * can lead to memory leaks and unexpected behavior.
     *
     * @see ConnectivityManager.registerNetworkCallback
     * @see networkCallback
     */
    fun stopMonitoring() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun handleNetworkAvailability() {
        val networkType = getNetworkType()
        if (showTrafficBubble) {
            Log.d(TAG, "Starting floating bubble service :: $networkType")
            stopFloatingBubbleService()
            startFloatingBubbleService(networkType)
        }
    }

    private fun handleNetworkLoss() {
        Log.d(TAG, "Stopping floating bubble service")
        stopFloatingBubbleService()
    }

    private fun startFloatingBubbleService(networkType: String?) {
        val serviceIntent = Intent(context, FloatingBubbleService::class.java).apply {
            putExtra(NETWORK_TYPE_EXTRA, networkType)
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    private fun stopFloatingBubbleService() {
        val serviceIntent = Intent(context, FloatingBubbleService::class.java)
        context.stopService(serviceIntent)
    }

    private fun getNetworkType(): String? {
        val activeNetwork = connectivityManager.activeNetwork ?: return null
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return null

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile"
            else -> null
        }
    }
}