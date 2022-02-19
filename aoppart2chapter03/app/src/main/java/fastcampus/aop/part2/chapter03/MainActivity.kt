package fastcampus.aop.part2.chapter03

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private val numberPicker1: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker1)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }
    private val numberPicker2: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker2)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }
    private val numberPicker3: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker3)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }

    private val openButton: AppCompatButton by lazy {
        findViewById(R.id.openButton)
    }

    private val changePwButton: AppCompatButton by lazy {
        findViewById(R.id.changePwButton)
    }

    private var changePasswordMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker1
        numberPicker2
        numberPicker3

        openButton.setOnClickListener {
            if(changePasswordMode){
                Toast.makeText(this,"비밀번호 변경 중", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //파일에 저장을 하여 가져온다. 이름, 모드(다른곳에서 사용 할 수 있는지)
            val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)

            val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            //값을 가져오고 default 값 설정
            if(passwordPreference.getString("password","000").equals(passwordFromUser)){
                //패스워드 성공
                //TODO 다이어리 페이지 작성 후 넘겨줘야함
                startActivity(Intent(this, DiaryActivity::class.java))
            }else {
                showErrorAlertDialog()
                //실패
            }

        }

        changePwButton.setOnClickListener {
            val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)
            val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            if(changePasswordMode){
                //번호를 저장하는 기능

                passwordPreference.edit {
                    putString("password",passwordFromUser)
                    commit()
                }

                changePasswordMode = false
                changePwButton.setBackgroundColor(Color.BLACK)


            } else{
                //changePasswordMode가 활성화 :: 비밀번호가 맞는지 확인

                //값을 가져오고 default 값 설정
                if(passwordPreference.getString("password","000").equals(passwordFromUser)){
                    changePasswordMode = true
                    Toast.makeText(this, "변경할 패스워드 입력하세요", Toast.LENGTH_SHORT).show()
                    changePwButton.setBackgroundColor(Color.RED)
                }else {
                    showErrorAlertDialog()
                    //실패
                }
            }
        }
    }

    private fun showErrorAlertDialog(){
        AlertDialog.Builder(this)
            .setTitle("실패")
            .setMessage("비밀번호가 잘못되었습니다.")
            .setPositiveButton("확인"){ _, _ -> } //두개의 인자를 받음
            .create()
            .show()
    }
}