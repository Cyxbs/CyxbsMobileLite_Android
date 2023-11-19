package com.cyxbs.components.view.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import com.cyxbs.components.base.ui.CyxbsBaseDialog
import com.cyxbs.components.view.R
import com.g985892345.android.extensions.android.size.dp2px
import com.google.android.material.button.MaterialButton

/**
 * 支持自定义内容视图的圆角 dialog，该 dialog 样式符合视觉要求的大部分场景
 *
 * 继承于 ComponentDialog，支持 LifecycleOwner，对应的也支持协程
 *
 * 你可以参考它的实现类 [BaseChooseDialog]、DebugUpdateDialog 和 UserAgreementDialog 来适配你需要的场景
 *
 * ## 1、为什么不用 DialogFragment ?
 * 虽然官方推荐使用 DialogFragment，但是 Fragment 与父容器通信很麻烦，并且目前掌邮强制竖屏，所以不打算使用 DialogFragment
 *
 * ## 2、DialogFragment 推荐用法
 * DialogFragment 主要有两个坑
 * - Fragment 重建导致的数据丢失问题
 * - Fragment 不好与父容器直接通信
 *
 * 问题一可以采用 ViewModel 或者 savedInstanceState 解决
 * 问题二可以采用 ViewModel 或者 DialogFragment 直接 getActivity()/getParentFragment() 强转解决(强烈建议用接口约束下有哪些方法)
 *
 * ## 3、本 Dialog 可直接变为 DialogFragment
 * DialogFragment 提供了 onCreateDialog(): Dialog 方法用于自定义 Dialog，如果有必要的话，可以重写该方法。
 * 但请遵守 Fragment 的使用规范 (详细请查看飞书易错点文档)
 * 请查看：https://redrock.feishu.cn/wiki/wikcnSDEtcCJzyWXSsfQGqWxqGe
 *
 *
 *
 * 更多注释请查看 [BaseChooseDialog]，自定义 dialog 可以参考 DebugUpdateDialog
 *
 * @author 985892345
 * 2022/12/29 20:08
 */
abstract class BaseChooseDialog<T : BaseChooseDialog<T, D>, D: BaseChooseDialog.Data> : CyxbsBaseDialog {

  protected constructor(context: Context) : super(context)

  protected constructor(context: Context, @StyleRes themeResId: Int) : super(context, themeResId)
  
  /**
   * 创建显示的内容，你可以在这里面返回你自己的内容视图
   */
  abstract fun createContentView(parent: ViewGroup): View
  
  /**
   * @param view 你在 [createContentView] 返回的 View
   */
  abstract fun initContentView(view: View)
  
  private var _data: D? = null
  
  protected val data: D get() = _data!!
  protected var positiveClick: (T.() -> Unit)? = null
  protected var negativeClick: (T.() -> Unit)? = null
  protected var dismissCallback: (T.() -> Unit)? = null
  protected var cancelCallback: (T.() -> Unit)? = null
  
  override fun onCreate(savedInstanceState: Bundle?) {
    // 取消 dialog 默认背景
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    super.onCreate(savedInstanceState)

    val view = LayoutInflater.from(context).inflate(data.type.layoutId, null)
    val parent = view.findViewById<FrameLayout>(R.id.config_fl_choose_dialog_btn_content)
    val insertView = createContentView(parent)
    parent.addView(insertView)
    setContentView(
      view,
      ViewGroup.LayoutParams(
        data.width.let { if (it > 0) it.dp2px else it },
        data.height.let { if (it > 0) it.dp2px else it }
      )
    )
    initViewInternal(view)
    initContentView(insertView)
  }
  
  @Suppress("UNCHECKED_CAST")
  private fun initViewInternal(view: View) {
    // 根据不同类型进行不同的设置
    when (data.type) {
      DialogType.ONE_BUT -> {
        val ivBg: ImageView = view.findViewById(R.id.config_iv_choose_dialog_one_btn_background)
        val btnPositive: MaterialButton =
          view.findViewById(R.id.config_btn_choose_dialog_one_btn_positive)
        ivBg.setImageResource(data.backgroundId)
        btnPositive.text = data.positiveButtonText
        btnPositive.layoutParams.apply {
          width = data.buttonWidth.let { if (it > 0) it.dp2px else it }
          height = data.buttonHeight.let { if (it > 0) it.dp2px else it }
        }
        btnPositive.setBackgroundColor(
          ContextCompat.getColor(context, data.positiveButtonColor)
        )
        btnPositive.setOnClickListener {
          positiveClick?.invoke(this as T)
        }
      }
      DialogType.TWO_BUT -> {
        val ivBg: ImageView = view.findViewById(R.id.config_iv_choose_dialog_two_btn_background)
        val btnPositive: MaterialButton =
          view.findViewById(R.id.config_btn_choose_dialog_two_btn_positive)
        val btnNegative: MaterialButton =
          view.findViewById(R.id.config_btn_choose_dialog_two_btn_negative)
        ivBg.setImageResource(data.backgroundId)
        btnPositive.text = data.positiveButtonText
        btnNegative.text = data.negativeButtonText
        btnPositive.layoutParams.apply {
          width = data.buttonWidth.let { if (it > 0) it.dp2px else it }
          height = data.buttonHeight.let { if (it > 0) it.dp2px else it }
        }
        btnNegative.layoutParams.apply {
          width = data.buttonWidth.let { if (it > 0) it.dp2px else it }
          height = data.buttonHeight.let { if (it > 0) it.dp2px else it }
        }
        btnPositive.setBackgroundColor(
          ContextCompat.getColor(context, data.positiveButtonColor)
        )
        btnNegative.setBackgroundColor(
          ContextCompat.getColor(context, data.negativeButtonColor)
        )
        btnPositive.setOnClickListener {
          positiveClick?.invoke(this as T)
        }
        btnNegative.setOnClickListener {
          negativeClick?.invoke(this as T)
        }
      }
    }
  }
  
  @Suppress("UNCHECKED_CAST")
  override fun dismiss() {
    super.dismiss()
    dismissCallback?.invoke(this as T)
  }
  
  @Suppress("UNCHECKED_CAST")
  override fun cancel() {
    super.cancel()
    cancelCallback?.invoke(this as T)
  }

  /**
   * 为什么设计 Builder ?
   * 如果直接给 dialog 添加这些方法，会导致后面使用时仍然可以修改
   */
  abstract class Builder<T : BaseChooseDialog<T, D>, D: Data>(
    protected val context: Context,
    protected val data: D
  ) {

    protected var positiveClick: (T.() -> Unit)? = null
    protected var negativeClick: (T.() -> Unit)? = null
    protected var dismissCallback: (T.() -> Unit)? = null
    protected var cancelCallback: (T.() -> Unit)? = null

    /**
     * 设置确定按钮的点击监听
     */
    open fun setPositiveClick(click: T.() -> Unit): Builder<T, D> {
      positiveClick = click
      return this
    }

    /**
     * 设置取消按钮的点击监听
     */
    open fun setNegativeClick(click: T.() -> Unit): Builder<T, D> {
      negativeClick = click
      return this
    }

    /**
     * 设置所有关闭 dialog 的回调, 包括调用 dismiss() 和 返回键
     */
    open fun setDismissCallback(call: T.() -> Unit): Builder<T, D> {
      dismissCallback = call
      return this
    }

    /**
     * 设置只包含返回键关闭 Dialog 的回调
     */
    open fun setCancelCallback(call: T.() -> Unit): Builder<T, D> {
      cancelCallback = call
      return this
    }

    fun build(): T {
      val dialog = buildInternal()
      dialog._data = data
      dialog.positiveClick = positiveClick
      dialog.negativeClick = negativeClick
      dialog.dismissCallback = dismissCallback
      dialog.cancelCallback = cancelCallback
      return dialog
    }

    // 用于生成  Dialog 对应对象
    protected abstract fun buildInternal(): T

    /**
     * 直接展示
     */
    fun show() {
      build().show()
    }
  }
  
  /**
   * 如果不考虑继承，简单的 dialog 你可以这样实现
   * ```
   * class Data(
   *     override val width: Int = 320,
   *     override val height: Int = 300,
   * ) : BaseChooseDialog.Data by BaseChooseDialog.Data // 使用 by 进行接口代理
   * ```
   */
  interface Data {
    /**
     * Dialog 类型
     */
    val type: DialogType
      get() = DialogType.TWO_BUT
    
    /**
     * 确定按钮文本，默认为
     */
    val positiveButtonText: String
      get() = "确定"
    
    /**
     * 取消按钮文本
     */
    val negativeButtonText: String
      get() = "取消"
    
    /**
     * 确定按钮颜色
     */
    val positiveButtonColor: Int
      get() = R.color.view_choose_dialog_btn_positive
    
    /**
     * 取消按钮颜色
     */
    val negativeButtonColor: Int
      get() = R.color.view_choose_dialog_btn_negative

    /**
     * button 的按钮宽
     * - 默认为 80dp
     * - 单位 dp
     * - 支持 LayoutParams.WRAP_CONTENT、LayoutParams.MATCH_PARENT
     */
    val buttonWidth: Int
      get() = 80

    /**
     * button 的按钮大小
     * - 默认为 34dp
     * - 单位 dp
     * - 支持 LayoutParams.WRAP_CONTENT、LayoutParams.MATCH_PARENT
     */
    val buttonHeight: Int
      get() = 34
    
    /**
     * dialog 的宽
     * - 默认为 300dp
     * - 单位 dp
     * - 支持 LayoutParams.WRAP_CONTENT、LayoutParams.MATCH_PARENT
     */
    val width: Int
      get() = 300
    
    /**
     * dialog 的高
     * - 默认为 wrap_content
     * - 单位 dp
     * - 支持 LayoutParams.WRAP_CONTENT、LayoutParams.MATCH_PARENT
     */
    val height: Int
      get() = ViewGroup.LayoutParams.WRAP_CONTENT
    
    /**
     * dialog 的背景，默认背景应该能满足
     */
    @get:DrawableRes
    val backgroundId: Int
      get() = R.drawable.view_shape_round_corner_dialog
    
    companion object : Data
  }
  
  enum class DialogType(val layoutId: Int) {
    // 只有一个 Button
    ONE_BUT(R.layout.view_choose_dialog_one_btn),
    
    // 有两个 Button
    TWO_BUT(R.layout.view_choose_dialog_two_btn)
  }
}