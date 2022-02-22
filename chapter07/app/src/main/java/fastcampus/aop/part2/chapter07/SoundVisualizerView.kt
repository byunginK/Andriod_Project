package fastcampus.aop.part2.chapter07

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SoundVisualizerView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    //곡선이 부드럽게 그려진다.
    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.purple_500)
        strokeWidth = LINE_WIDTH
        strokeCap = Paint.Cap.ROUND //양끝 처리
    }
    var drawingWidth: Int = 0
    var drawingHeight: Int = 0
    private var drawingAmplitudes: List<Int> = emptyList()

    var onRequestCurrentAmplitude: (() -> Int)? = null

    private var isReplaying: Boolean = false
    private var replayingPosition = 0


    //별도 쓰레드로 계속 가져옴
    private val visualizeRepeatAction: Runnable = object : Runnable {
        override fun run() {
            if(!isReplaying){
                //onRequestCurrentAmplitude을 통해서 mainActivity에서 받은 레코드 값을 받아온다.
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0
                //Amplitude, Draw
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
            }else{
                replayingPosition++
            }

            invalidate() //새로운 데이터를 받았을때 다시 draw해주게끔 해준다.

            handler?.postDelayed(this, ACTION_INTERVAL)
        }
    }
    //view의 화면크기가 변화 할때마자 사이즈 값을 파라미터로 return
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingWidth = w
        drawingHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //음량의 크기에 따라 값을 가지고 그려줌

        canvas ?: return

        val centerY = drawingHeight / 2f
        var offsetX = drawingWidth.toFloat()

        drawingAmplitudes
            .let {
                if(isReplaying){
                    it.takeLast(replayingPosition)
                }else{
                    it
                }
            }
            .forEach {
            val lineLength = it / MAX_AMPLITUDE * drawingHeight * 0.8F

            offsetX -= LINE_SPACE
            if (offsetX < 0) return@forEach

            //라인을 그리는 부분
            canvas.drawLine(
                offsetX,
                centerY - lineLength / 2F,
                offsetX,
                centerY + lineLength / 2F,
                amplitudePaint
            )
        }
    }

    fun startVisualizing(isReplaying: Boolean) {
        this.isReplaying = isReplaying
        handler?.post(visualizeRepeatAction)
    }

    fun stopVisualizing() {
        replayingPosition = 0
        handler?.removeCallbacks(visualizeRepeatAction)
    }

    fun clearVisualization(){
        drawingAmplitudes = emptyList()
        invalidate()
    }

    companion object {
        //음량 작대기 기본 굵기와 간격
        private const val LINE_WIDTH = 10F //굵기
        private const val LINE_SPACE = 15F //간격
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat() //음량의 최대값은 Short타입의 max값이다.
        private const val ACTION_INTERVAL = 20L
    }
}