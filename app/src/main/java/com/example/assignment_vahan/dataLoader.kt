import com.example.assignment_vahan.ApiInterface
import com.example.assignment_vahan.Items
import com.example.assignment_vahan.MainActivity
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class dataLoader {
    val retrofit = Retrofit.Builder()
        .baseUrl(MainActivity.Base_Url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiService = retrofit.create(ApiInterface::class.java)

    suspend fun fetchData(): Response<ArrayList<Items>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getData().execute()
                response
            } catch (e: Exception) {
                throw e
            }
        }
    }
}
