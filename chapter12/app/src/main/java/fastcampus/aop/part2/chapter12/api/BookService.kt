package fastcampus.aop.part2.chapter12.api

import fastcampus.aop.part2.chapter12.model.BestSellerDto
import fastcampus.aop.part2.chapter12.model.SearchBookDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BookService {
    @GET("/api/search.api?&output=json")
    fun getBooksByName(
        @Query("key") apiKey: String,
        @Query("query")keyword: String
    ): Call<SearchBookDto>

    @GET("/api/bestSeller.api?&categoryId=100&output=json")
    fun getBestSellerBooks(
        @Query("key") apiKey: String
    ):Call<BestSellerDto>
}