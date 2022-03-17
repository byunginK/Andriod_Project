package fastcampus.aop.part3.chapter17.service

import retrofit2.Call
import retrofit2.http.GET

interface MusicService {

    @GET("/v3/9f2c8848-c49f-46a1-a38b-b7f71d215f2e")
    fun listMusics(): Call<MusicDto>
}