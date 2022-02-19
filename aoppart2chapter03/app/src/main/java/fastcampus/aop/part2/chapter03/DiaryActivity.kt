package fastcampus.aop.part2.chapter03

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class DiaryActivity: AppCompatActivity() {

    //main UI쓰레드와 별도 쓰레드를 핸들러를 통해서 연결
    private val handler = Handler(Looper.getMainLooper())

    private val diaryEditText: EditText by lazy {
        findViewById(R.id.diaryEditText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val detailPreferences = getSharedPreferences("diary", Context.MODE_PRIVATE)

        diaryEditText.setText(detailPreferences.getString("detail", ""))

        //별도 쓰레드로 잠깐 멈췄을때 저장
        val runnable = Runnable {
            getSharedPreferences("diary",Context.MODE_PRIVATE).edit {
                putString("detail", diaryEditText.text.toString())
            }
            Log.d("DiaryActivity", "SAVE ${diaryEditText.text.toString()}")
        }

        //텍스트가 바뀔때마다 이벤트 작동
        diaryEditText.addTextChangedListener {
            Log.d("DiaryActivity", "TextCahnged::$it")
            handler.removeCallbacks(runnable) //이전에 있는 변화를 우선 지운다
            handler.postDelayed(runnable, 500)
        }
    }
}