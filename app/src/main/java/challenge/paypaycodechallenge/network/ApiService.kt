package challenge.paypaycodechallenge.network

import challenge.paypaycodechallenge.models.GetCurrenciesResponse
import challenge.paypaycodechallenge.models.GetRatesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("/live")
    fun getExchangeRates(@Query("access_key") access_key: String): Call<GetRatesResponse>

    @GET("/list")
    fun getSupportedCurrencies(@Query("access_key") access_key: String): Call<GetCurrenciesResponse>
}