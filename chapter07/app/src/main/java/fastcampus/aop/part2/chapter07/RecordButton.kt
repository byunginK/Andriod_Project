package fastcampus.aop.part2.chapter07

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

//AppCompatImageButton 안드로이드 호환성 문제로 다른 버전에도 적용하기위한 클래스 상속
class RecordButton(context: Context, attrs: AttributeSet): AppCompatImageButton(context, attrs) {

    init {
        setBackgroundResource(R.drawable.shape_oval_button)
    }

    fun updateIconWithState(state: State){
        when(state){
            State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.ic_record)
            }
            State.ON_RECORDING -> {
                setImageResource(R.drawable.ic_stop)
            }
            State.AFTER_RECORDING -> {
                setImageResource(R.drawable.ic_play)
            }
            State.ON_PLAYING -> {
                setImageResource(R.drawable.ic_stop)
            }
        }
    }
}