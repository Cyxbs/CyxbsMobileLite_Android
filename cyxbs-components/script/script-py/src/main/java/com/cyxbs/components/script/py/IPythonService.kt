package com.cyxbs.components.script.py

/**
 * .
 *
 * @author 985892345
 * @date 2023/11/19 00:03
 */
interface IPythonService {

  suspend fun evaluatePy(py: String): String
}