package fastcampus.aop.part2.chapter16.service

import fastcampus.aop.part2.chapter16.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {
    @GET("/v3/70c455a4-2101-404b-8387-c26744299b52")
    fun listVideos(): Call<VideoDto>
}