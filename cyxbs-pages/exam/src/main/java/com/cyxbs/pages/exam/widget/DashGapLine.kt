package com.cyxbs.pages.exam.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.g985892345.android.extensions.android.size.dp2pxF

/**
 * 侧边带缝隙的图形
 *
 * @author 985892345
 * 2023/4/8 15:52
 */
class DashGapLine(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
  
  private val mRadius = 7.dp2pxF
  private val mCircleY = mRadius + 5.dp2pxF
  
  private val mCirclePaint by lazy {
    Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.parseColor("#2921D1")
      strokeWidth = 5.dp2pxF
      style = Paint.Style.STROKE
    }
  }
  
  private val mGapPaint by lazy {
    Paint().apply {
      color = Color.parseColor("#2921D1")
      strokeWidth = 3F
      style = Paint.Style.STROKE
      pathEffect = DashPathEffect(floatArrayOf(15F, 15F), 0F)
    }
  }
  
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val cx = width / 2F
    canvas.drawCircle(cx, mCircleY, mRadius, mCirclePaint)
    canvas.drawLine(cx, mRadius * 2 + 11, cx, height.toFloat(), mGapPaint)
  }
}