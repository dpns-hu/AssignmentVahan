        package com.example.assignment_vahan
        import android.app.ActivityManager
        import android.content.BroadcastReceiver
        import android.content.ComponentName
        import android.content.Context
        import android.content.Intent
        import android.content.IntentFilter
        import android.content.ServiceConnection
        import android.os.Bundle
        import android.os.IBinder
        import android.util.Log
        import android.view.View
        import android.widget.Toast
        import androidx.appcompat.app.AppCompatActivity
        import androidx.core.content.ContextCompat.getSystemService
        import androidx.recyclerview.widget.LinearLayoutManager
        import androidx.recyclerview.widget.RecyclerView
        import com.example.assignment_vahan.ApiInterface
        import com.example.assignment_vahan.Items
        import com.example.assignment_vahan.MyAdapter
        import com.example.assignment_vahan.databinding.ActivityMainBinding
        import com.example.assignment_vahan.webviewActivity
        import kotlinx.coroutines.CoroutineScope
        import kotlinx.coroutines.Dispatchers
        import kotlinx.coroutines.GlobalScope
        import kotlinx.coroutines.launch
        import retrofit2.Call
        import retrofit2.Callback
        import retrofit2.Response
        import retrofit2.Retrofit
        import retrofit2.converter.gson.GsonConverterFactory
        import kotlin.system.exitProcess

        class MainActivity : AppCompatActivity() {
            companion object {
                var Base_Url = "http://universities.hipolabs.com/"
            }

            lateinit var binding: ActivityMainBinding

            lateinit var newlist: ArrayList<Items>
            private var dataRefreshService: DataRefresh? = null
            lateinit var adpter: MyAdapter
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                binding = ActivityMainBinding.inflate(layoutInflater)
                setContentView(binding.root)
                binding.progressBarrvId.visibility = View.VISIBLE
                binding.recyclerviewId.layoutManager = LinearLayoutManager(this)
                binding.recyclerviewId.setHasFixedSize(true)
                binding.recyclerviewId.setItemViewCacheSize(10)
                adpter = MyAdapter(this, DataStorage.getDataList())

                if (!isServiceRunning(DataRefresh::class.java)) {
                    val intent = Intent(this, DataRefresh::class.java)
                    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                    startService(intent)

                }
                loadDataInBackground()
                val filter = IntentFilter("com.example.assignment_vahan.DATA_UPDATED")
                registerReceiver(dataUpdateReceiver, filter)

            binding.closeId.setOnClickListener {
             stopService()
                finish()
            }

            }
            private fun stopService() {
                val serviceIntent = Intent(this, DataRefresh::class.java)
                unbindService(serviceConnection) // Unbind from the service
                stopService(serviceIntent) // Stop the service
            }
        private fun isServiceRunning(serviceClass: Class<*>): Boolean {
            val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }
        private val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as DataRefresh.LocalBinder
                dataRefreshService = binder.getService()

            }

            override fun onServiceDisconnected(name: ComponentName?) {
                dataRefreshService = null

            }
        }

            private fun loadDataInBackground() {
                // Use coroutines to run the network request in a background thread
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val retrofit = Retrofit.Builder()
                            .baseUrl(Base_Url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                        val apiService = retrofit.create(ApiInterface::class.java)
                        val response = apiService.getData().execute()

                        // Switch back to the main thread to update UI
                        launch(Dispatchers.Main) {
                            handleResponse(response)
                        }
                    } catch (e: Exception) {
                        Log.e("error", "Exception: ${e.message}", e)
                        // Handle the exception, e.g., show an error message to the user
                    }
                }
            }
            private val dataUpdateReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == "com.example.assignment_vahan.DATA_UPDATED") {
                        // Extract the updated list from the intent
                        if (DataStorage.getDataList() != null) {
                            // Update the data in your adapter
                            adpter.list = DataStorage.getDataList()
                            binding.totalId.text = "Total Items ${DataStorage.getDataList().size}"
                            // Notify the adapter that the data has changed
                            adpter.notifyDataSetChanged()
                        }
                    }
                }
            }

            private fun handleResponse(response: Response<ArrayList<Items>>) {
                if (response.isSuccessful) {

                    val data = response.body()
                    data?.let {
                        DataStorage.updateDataList(it)
                    }
                    binding.recyclerviewId.adapter =
                        MyAdapter(this@MainActivity, DataStorage.getDataList())

                    binding.progressBarrvId.visibility = View.GONE
                    binding.totalId.text = "Total Items ${DataStorage.getDataList().size}"
                } else {
                    Log.d("error", "Failed")

                }
            }



                }




