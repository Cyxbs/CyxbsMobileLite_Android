package com.cyxbs.components.init

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/20 23:46
 */
interface IInitialService {

  /**
   * 主进程 Application 初始化时回调
   */
  fun onMainProcess(manager: IInitialManager) {}

  /**
   * 其他进程 Application 初始化时回调
   */
  fun onOtherProcess(manager: IInitialManager) {}
}