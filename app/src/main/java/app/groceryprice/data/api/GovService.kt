package app.groceryprice.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class GovService {
    private val BASE_URL = " https://api.data.gov.in/resource/"
    fun getGovService(): GovApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GovApi::class.java)
    }
}