package fastcampus.aop.part2.chapter16

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout

//전체 화면이 스와프되는 문제를 위해 커스텀 생성
class CustomMotionLayout(context: Context, attributeSet: AttributeSet? = null):MotionLayout(context,attributeSet) {
    private var motionTouchStarted = false
    private val mainContainerView by lazy {
        findViewById<View>(R.id.mainContainerLayout)
    }
    private val hitRect = Rect()

    init {
        setTransitionListener(object : TransitionListener{
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {

            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {

            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                motionTouchStarted = false
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {

            }

        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.actionMasked) {
            //ACTION_UP , ACTION_CANCEL 은 터치를 안하는 액션
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                motionTouchStarted = false
                return super.onTouchEvent(event) //원래값을 리턴 (사용 x)
            }
        }
        if(!motionTouchStarted){
            //getHitRect() = 상위 요소의 좌표에서 하위 요소의 적중 사각형(터치 가능한 영역)을 가져옵니다
            mainContainerView.getHitRect(hitRect)
            //rect.contains = Returns true if (x,y) is inside the rectangle
            motionTouchStarted = hitRect.contains(event.x.toInt(), event.y.toInt())
        }
        return super.onTouchEvent(event) && motionTouchStarted
    }

    private val gestureListener by lazy {
        object: GestureDetector.SimpleOnGestureListener(){
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                mainContainerView.getHitRect(hitRect)
                return hitRect.contains(e1.x.toInt(),e1.y.toInt())
            }
        }
    }
    private val gestureDetector by lazy {
        /*
        Detects various gestures and events using the supplied MotionEvents.
        The OnGestureListener callback will notify users when a particular motion event has occurred.
        This class should only be used with MotionEvents reported via touch
        */
        GestureDetector(context, gestureListener)
    }
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}