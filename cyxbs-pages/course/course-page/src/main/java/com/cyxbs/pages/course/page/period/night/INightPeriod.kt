package com.cyxbs.pages.course.page.period.night

/**
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/17 1:07
 */
interface INightPeriod {
  
  /**
   * 遍历晚上时间段区域
   */
  fun forEachNight(block: (row: Int) -> Unit)
  
  /**
   * 跟晚上时间段作对比
   * @return 1、返回正数，说明 [row] 在晚上时间段下面；2、返回负数，说明在上面；3、返回 0，说明在里面
   */
  fun compareNightPeriodByRow(row: Int): Int
  
  /**
   * 同 [compareNightPeriodByRow]，但比较的是高度 [height]
   */
  fun compareNightPeriodByHeight(height: Int): Int
  
  /**
   * 得到晚上时间段开始时的高度值（距离课表上边缘）
   */
  fun getNightStartHeight(): Int
  
  /**
   * 得到晚上时间段结束时的高度值（距离课表上边缘）
   */
  fun getNightEndHeight(): Int
}