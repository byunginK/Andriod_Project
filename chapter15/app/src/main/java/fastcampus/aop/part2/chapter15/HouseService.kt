package fastcampus.aop.part2.chapter15

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {
    @GET("/v3/bdfb448d-4e39-4847-802a-552e6b17255a")
    fun getHouseList(): Call<HouseDto>
}