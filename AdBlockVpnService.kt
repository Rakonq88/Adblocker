package com.manus.adblocker

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log

class AdBlockVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private val TAG = "AdBlockVpnService"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "START") {
            startVpn()
        } else if (intent?.action == "STOP") {
            stopVpn()
        }
        return START_STICKY
    }

    private fun startVpn() {
        try {
            val builder = Builder()
            builder.setSession("Manus AdBlocker")
            // تعيين عنوان IP محلي للواجهة
            builder.addAddress("10.0.0.2", 32)
            // توجيه كل حركة المرور عبر الـ VPN ليتم فحصها (أو توجيه الـ DNS فقط)
            builder.addRoute("0.0.0.0", 0)
            
            // استخدام خوادم DNS الخاصة بـ AdGuard التي تقوم بالحجب تلقائياً
            // هذا هو الحل الأكثر فعالية وسهولة في التنفيذ البرمجي
            builder.addDnsServer("94.140.14.14")
            builder.addDnsServer("94.140.15.15")

            vpnInterface = builder.establish()
            Log.i(TAG, "VPN Interface established with AdGuard DNS")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start VPN", e)
        }
    }

    private fun stopVpn() {
        try {
            vpnInterface?.close()
            vpnInterface = null
            stopSelf()
            Log.i(TAG, "VPN Stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping VPN", e)
        }
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }
}
