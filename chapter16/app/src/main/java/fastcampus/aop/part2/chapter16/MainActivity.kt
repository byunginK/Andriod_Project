package fastcampus.aop.part2.chapter16

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fastcampus.aop.part2.chapter16.adapter.VideoAdapter
import fastcampus.aop.part2.chapter16.dto.VideoDto
import fastcampus.aop.part2.chapter16.service.VideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //fragment 레이아웃을 붙여주기
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PlayerFragment())
            .commit()
        //아래 fragments들 중에서 PlayerFragment를 찾고 찾은 첫번째는 사용
        videoAdapter = VideoAdapter(callback = { url, title ->
            supportFragmentManager.fragments.find {
                it is PlayerFragment
            }?.let {
                (it as PlayerFragment).play(url, title)
            }
        })
        findViewById<RecyclerView>(R.id.mainRecycleView).apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
        //비디오 목록 가져오기
        getVideoList()
    }

    private fun getVideoList() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also {
            it.listVideos()
                .enqueue(object : Callback<VideoDto> {
                    override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                        if (response.isSuccessful.not()) {
                            Log.d("MainActivity", "response fail")
                            return
                        }

                        response.body()?.let { videoDto ->
                            videoAdapter.submitList(videoDto.videos)
                        }
                    }

                    override fun onFailure(call: Call<VideoDto>, t: Throwable) {

                    }

                })
        }
    }
}