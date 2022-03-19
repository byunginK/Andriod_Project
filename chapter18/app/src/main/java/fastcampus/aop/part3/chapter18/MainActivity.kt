package fastcampus.aop.part3.chapter18

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import fastcampus.aop.part3.chapter18.MapActivity.Companion.SEARCH_RESULT_EXTRA_KEY
import fastcampus.aop.part3.chapter18.databinding.ActivityMainBinding
import fastcampus.aop.part3.chapter18.model.LocationLatLngEntity
import fastcampus.aop.part3.chapter18.model.SearchResultEntity
import fastcampus.aop.part3.chapter18.response.search.Poi
import fastcampus.aop.part3.chapter18.response.search.Pois
import fastcampus.aop.part3.chapter18.utillity.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

//CoroutineScope 비동기 통신을 사용
class MainActivity : AppCompatActivity(),CoroutineScope {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: SearchRecyclerAdapter
    private lateinit var job: Job

    //코루틴 context 오버라이드
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job() //객체 생성
        initAdapter()
        initView()
        bindViews()
        initData()
    }

    //with은 scope 함수
    private fun initView() = with(binding){
        emptyResultTextView.isVisible = false
        recyclerView.adapter = adapter
    }

    //클릭 시점 함수 실행
    private fun bindViews() = with(binding){
        searchButton.setOnClickListener {
            searchKeyword(searchBarInputView.text.toString())
        }
    }

    private fun initAdapter() {
        adapter = SearchRecyclerAdapter()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData(){
        adapter.notifyDataSetChanged()
    }

    private fun setData(pois: Pois){
        val dataList = pois.poi.map {
            SearchResultEntity(
                name = it.name?:"빌딩명 없음",
                fullAdress = makeMainAdress(it),
                locationLatLng =  LocationLatLngEntity(
                    it.noorLat,
                    it.noorLon
                )
            )
        }
        adapter.setSearchResultList(dataList){
            Toast.makeText(this, "빌딩이름: ${it.name} 주소: ${it.fullAdress} 위도/경도 : ${it.locationLatLng}",Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this,MapActivity::class.java).apply {
                    putExtra(SEARCH_RESULT_EXTRA_KEY,it)
                }
            )

        }
    }

    private fun searchKeyword(keywordString: String){
        //coroutineContext를 통해 처음에는 main 쓰레드에서 실행
        launch(coroutineContext) {
            try {
                //IO 쓰레드로 변경
                withContext(Dispatchers.IO){
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        keyword = keywordString
                    )
                    if(response.isSuccessful){
                        val body = response.body()
                        //비동기로 받아온 후 dispatchers를 다시 main으로 바꿔준다
                        withContext(Dispatchers.Main){
                            //Log.e("response", body.toString())
                            body?.let { searchResponse ->
                                setData(searchResponse.searchPoiInfo.pois)
                            }
                        }
                    }
                }
            }catch (e: Exception){

            }
        }
    }
    private fun makeMainAdress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }
}