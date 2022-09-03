package app.groceryprice.data.api

import app.groceryprice.data.model.GovList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// https://api.data.gov.in/resource/9ef84268-d588-465a-a308-a864a43d0070?api-key=579b464db66ec23bdd000001643c337cb1e54a9840cf74338d06efe3&format=json&offset=0&limit=100
// https://api.data.gov.in/resource/9ef84268-d588-465a-a308-a864a43d0070?filters%5Bmarket%5D=Agra&api-key=API_KEY&format=json&offset=2&limit=10

interface GovApi {
    @GET("9ef84268-d588-465a-a308-a864a43d0070")
    suspend fun getList(
        @Query("api-key") apiKey: String,
        @Query("format") format: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: String
    ): Response<GovList>

    @GET("9ef84268-d588-465a-a308-a864a43d0070")
    suspend fun getFilteredListByMarket(
        @Query("filters[market]") filterBy: String,
        @Query("api-key") apiKey: String,
        @Query("format") format: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: String
    ): Response<GovList>


    @GET("9ef84268-d588-465a-a308-a864a43d0070")
    suspend fun getFilteredListByDistrict(
        @Query("filters[district]") filterBy: String,
        @Query("api-key") apiKey: String,
        @Query("format") format: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: String
    ): Response<GovList>
}