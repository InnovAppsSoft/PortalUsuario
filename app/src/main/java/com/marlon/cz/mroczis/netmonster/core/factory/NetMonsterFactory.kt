package com.marlon.cz.mroczis.netmonster.core.factory

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.marlon.cz.mroczis.netmonster.core.INetMonster
import com.marlon.cz.mroczis.netmonster.core.NetMonster
import com.marlon.cz.mroczis.netmonster.core.telephony.*
import com.marlon.cz.mroczis.netmonster.core.telephony.TelephonyManagerCompat14
import com.marlon.cz.mroczis.netmonster.core.telephony.TelephonyManagerCompat17
import com.marlon.cz.mroczis.netmonster.core.telephony.TelephonyManagerCompat29
import com.marlon.cz.mroczis.netmonster.core.telephony.TelephonyManagerCompat30
import com.marlon.cz.mroczis.netmonster.core.subscription.ISubscriptionManagerCompat
import com.marlon.cz.mroczis.netmonster.core.subscription.SubscriptionManagerCompat14
import com.marlon.cz.mroczis.netmonster.core.subscription.SubscriptionManagerCompat22

/**
 * Factory that produces new instances.
 */
object NetMonsterFactory {

    /**
     * Creates new instance of [ITelephonyManagerCompat] that is bound to specified
     * subscription id ([subId]) if applicable for current Android version.
     */
    @SuppressLint("ObsoleteSdkInt")
    fun getTelephony(context: Context, subId: Int = Integer.MAX_VALUE): ITelephonyManagerCompat =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> TelephonyManagerCompat30(context, subId)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> TelephonyManagerCompat29(context, subId)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> TelephonyManagerCompat17(context, subId)
            else -> TelephonyManagerCompat14(context, subId)
        }

    fun getSubscription(context: Context) : ISubscriptionManagerCompat =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 -> SubscriptionManagerCompat22(context)
            else -> SubscriptionManagerCompat14(context)
        }

    /**
     * Creates new instance of [INetMonster].
     */
    fun get(context: Context) : INetMonster =
        NetMonster(context, getSubscription(context))

}