package fastcampus.aop.part2.chapter12

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import fastcampus.aop.part2.chapter12.adapter.BookAdapter
import fastcampus.aop.part2.chapter12.adapter.HistoryAdapter
import fastcampus.aop.part2.chapter12.api.BookService
import fastcampus.aop.part2.chapter12.databinding.ActivityMainBinding
import fastcampus.aop.part2.chapter12.model.BestSellerDto
import fastcampus.aop.part2.chapter12.model.History
import fastcampus.aop.part2.chapter12.model.SearchBookDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var bookService: BookService

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //리사이클 뷰를 가져와야한다.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBookRecyclerView()
        initHistoryRecyclerView()
        initSearchEditText()

        db = getAppDatabase(this)
        //데이터베이스를 추가


        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com")
                //바로 json형태로 변환하도록 설정
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)

        //아래 기능을 사용하기 위해서는 internet 권한을 줘야함
        bookService.getBestSellerBooks(getString(R.string.interparkAPIKey))
            .enqueue(object: Callback<BestSellerDto>{
                override fun onResponse(
                    call: Call<BestSellerDto>,
                    response: Response<BestSellerDto>
                ) {
                    //성공처리
                    if(response.isSuccessful.not()){
                        Log.e(TAG,"NOT SUCCESS!!")
                        return
                    }
                    response.body()?.let {
                        /*Log.d(TAG, it.toString())
                        it.books.forEach { book ->
                            Log.d(TAG, book.toString())
                        }*/
                        //currentList에 리스트를 넘겨준다.
                        adapter.submitList(it.books)
                    }
                }

                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
                    //실패처리
                    Log.e(TAG, t.toString() )
                }

            })


    }

    private fun initHistoryRecyclerView() {
        historyAdapter = HistoryAdapter(historyDeleteClickedListener = {
           deleteSearchKeyword(it)
        })
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun deleteSearchKeyword(keyWord: String) {
        Thread{
            db.historyDao().delete(keyWord)
            //todo veiw 갱신
            showHistoryView()
        }.start()
    }

    private fun search(keyWord: String) {
        bookService.getBooksByName(getString(R.string.interparkAPIKey),keyWord)
            .enqueue(object: Callback<SearchBookDto>{
                override fun onResponse(
                    call: Call<SearchBookDto>,
                    response: Response<SearchBookDto>
                ) {
                    hideHistoryView()
                    saveSearchKeyWord(keyWord)
                    //성공처리
                    if(response.isSuccessful.not()){
                        Log.e(TAG,"NOT SUCCESS!!")
                        return
                    }
                    //currentList에 리스트를 넘겨준다.
                    adapter.submitList(response.body()?.books.orEmpty())

                }

                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    //실패처리
                    hideHistoryView()
                    Log.e(TAG, t.toString() )
                }

            })
    }

    //리싸이클 뷰를 가져와 main activity뷰와 연동
    private fun initBookRecyclerView(){
        adapter = BookAdapter(itemClickedListener = {
            val intent = Intent(this,DetailActivity::class.java)
            //model 객체를 intent로 넘기려면 직렬화가 필요 (gradle parcelize)
            intent.putExtra("bookModel",it)
            startActivity(intent)
        })
        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = adapter
    }

    //insert 검색 기록
    private fun saveSearchKeyWord(keyWord: String){
        Thread{
            db.historyDao().insertHistory(History(null,keyWord))
        }.start()
    }

    private fun showHistoryView(){
        Thread{
            val keyWords = db.historyDao().getAll().reversed()
            runOnUiThread {
                binding.historyRecyclerView.isVisible = true
                historyAdapter.submitList(keyWords.orEmpty())
            }
        }.start()
        binding.historyRecyclerView.isVisible= true
    }

    private fun hideHistoryView(){
        binding.historyRecyclerView.isVisible = false
    }

    private fun initSearchEditText(){
        binding.searchEditText.setOnKeyListener { view, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN){
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true //반환값으로 true 제공
            }
            return@setOnKeyListener false
        }
        binding.searchEditText.setOnTouchListener { view, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_DOWN){
                showHistoryView()
            }
            return@setOnTouchListener false
        }
    }
    companion object{
        private const val TAG = "MainActivity"
    }
}