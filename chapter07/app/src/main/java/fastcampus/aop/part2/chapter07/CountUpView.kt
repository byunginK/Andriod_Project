package fastcampus.aop.part2.chapter07

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CountUpView(context: Context, attrs: AttributeSet? = null):AppCompatTextView(context, attrs) {

    private var startTimeStamp: Long = 0L

    private val countUPAction: Runnable = object :Runnable{
        override fun run() {
            val currentTimeStamp = SystemClock.elapsedRealtime()
            val countTimeSeconds = ((currentTimeStamp - startTimeStamp)/1000L).toInt()
            updateCountTime(countTimeSeconds)

            handler?.postDelayed(this, 1000L)
        }
    }

    fun startCountUp() {
        startTimeStamp = SystemClock.elapsedRealtime()
        handler?.post(countUPAction)
    }

    fun  stopCountUp(){
        handler?.removeCallbacks(countUPAction)
    }

    fun clearCountTime(){
        updateCountTime(0)
    }

    private fun updateCountTime(countTimeSeconds: Int){
        val minutes = countTimeSeconds / 60
        val seconds = countTimeSeconds % 60

        text = "%02d:%02d".format(minutes,seconds)
    }
}