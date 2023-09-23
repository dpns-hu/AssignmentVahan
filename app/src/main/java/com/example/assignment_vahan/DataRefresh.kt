package com.example.assignment_vahan

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.assignment_vahan.MainActivity.Companion.Base_Url
import dataLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

class DataRefresh : Service() {
    private val refreshIntervalMillis = 10 * 1000L // 10 seconds
    private val dataRefreshTimer = Timer()
    var list: ArrayList<Items> = ArrayList()
    private val dataLoader = dataLoader()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startDataRefresh()
        return START_STICKY
    }

    private fun startDataRefresh() {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Data Updated")
            .setContentText("Updating in every 10 seconds")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(FOREGROUND_SERVICE_ID, notification)

        // Schedule periodic data refresh using Timer
        dataRefreshTimer.scheduleAtFixedRate(DataRefreshTask(), 0, refreshIntervalMillis)
    }

    private inner class DataRefreshTask : TimerTask() {
        override fun run() {
            loadDataInBackground()
            Log.d("DataRefresh", "Data refreshed")
        }
    }

    private fun loadDataInBackground() {


            GlobalScope.launch(Dispatchers.IO) {
                try {
                  val response = dataLoader.fetchData()
                    launch(Dispatchers.Main) {

                        if (response.isSuccessful) {
                            val data = response.body()
                            data?.let {
                                list.clear()
                                list.addAll(it)
                                DataStorage.updateDataList(list)
                                val intent = Intent("com.example.assignment_vahan.DATA_UPDATED")
                                sendBroadcast(intent)
                            }
                        } else {
                            Log.d("error", "Failed")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("error", "Exception: ${e.message}", e)
                }
            }
        }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Data Refresh Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {

        stopDataRefresh()
        super.onDestroy()

    }
    inner class LocalBinder : Binder() {
        // Return the instance of the service
        fun getService(): DataRefresh = this@DataRefresh
    }
    private fun stopDataRefresh() {
        dataRefreshTimer.cancel()
        stopForeground(true)
        stopSelf()
    }

    companion object {
        private const val CHANNEL_ID = "DataRefreshChannel"
        private const val FOREGROUND_SERVICE_ID = 12345 // Unique ID for the foreground service
    }

}



