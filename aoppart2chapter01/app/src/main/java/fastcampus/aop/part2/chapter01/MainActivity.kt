package fastcampus.aop.part2.chapter01

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val heightEditText: EditText = findViewById(R.id.heightEditText)
        val weightEditText = findViewById<EditText>(R.id.weightEditText)

        val resultButton = findViewById<Button>(R.id.resultButton)

        resultButton.setOnClickListener {
            Log.d("MainActivity","ResultButton click")

            //만약 빈값을 그대로 받게되면 아래 toInt() 부분에서 값이 없어서 에러 발생
            if(heightEditText.text.isEmpty() || weightEditText.text.isEmpty()){
                Toast.makeText(this, "빈 값이 있습니다.",Toast.LENGTH_SHORT).show()
                //@setOnClickListener 라벨을 통해 return할 부분 지정
                return@setOnClickListener
            }

            val height: Int = heightEditText.text.toString().toInt()
            val weight: Int = weightEditText.text.toString().toInt()

            Log.d("MainActivity","height : $height , weight : $weight")

            //Intent에 다음 열릴 엑티비티 자바 파일을 담고 startActivity()로 시작을 해준다.
            //안드로이드 시스템으로 intent를 넘길때 값을 넘기고
            //받는쪽 activity에서 onCreate()할 때 intent에 포함된 값을 받을 수 있다.
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("height",height)
            intent.putExtra("weight",weight)

            startActivity(intent)
        }
    }
}