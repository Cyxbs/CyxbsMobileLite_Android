package com.cyxbs.pages.source.page.request

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import com.cyxbs.components.base.ui.CyxbsBaseFragment
import com.cyxbs.components.view.view.ScaleScrollTextView
import com.cyxbs.pages.source.R

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/29 20:39
 */
class InputOutputFragment : CyxbsBaseFragment(R.layout.source_fragment_input_output) {

  companion object {
    fun newInstance(
      input: List<Pair<String, String>>,
      output: String,
    ): InputOutputFragment {
      return InputOutputFragment().apply {
        arguments = bundleOf(
          this::mInput.name to input,
          this::mOutput.name to output,
        )
      }
    }
  }

  private val mInput by arguments<List<Pair<String, String>>>()
  private val mOutput by arguments<String>()

  private val mLlInput: LinearLayout by R.id.source_ll_input.view()
  private val mScaleScrollTextView: ScaleScrollTextView by R.id.source_sstv_output.view()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initInput()
    initOutput()
  }

  @SuppressLint("SetTextI18n")
  private fun initInput() {
    if (mInput.isEmpty()) {
      mLlInput.addView(TextView(requireContext()).apply {
        text = "无"
      })
    } else {
      mInput.forEach {
        mLlInput.addView(TextView(requireContext()).apply {
          text = "${it.first}: ${it.second}"
        })
      }
    }
  }

  private fun initOutput() {
    mScaleScrollTextView.text = mOutput
  }
}