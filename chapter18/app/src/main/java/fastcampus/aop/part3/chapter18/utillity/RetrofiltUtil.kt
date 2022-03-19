package fastcampus.aop.part3.chapter18.utillity

import fastcampus.aop.part3.chapter18.BuildConfig
import fastcampus.aop.part3.chapter18.Url
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitUtil {
    val apiService: ApiService by lazy {
        getRetrofit().create(ApiService::class.java)
    }

    //레트로핏을 생성하는 함수
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Url.TMAP_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildOkHttpClient())
            .build()

    }

    //인터셉터를 통해 매번 통신시 로깅을 찍는다.
    private fun buildOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        if(BuildConfig.DEBUG){
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        }else{
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }


}