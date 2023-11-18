package com.cyxbs.functions.api.script

/**
 * .
 *
 * @author 985892345
 * @date 2023/10/30 23:57
 */
interface IPythonService {

  suspend fun evaluatePy(py: String): String
}