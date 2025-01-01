package com.wirebarley.homework.util

import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

/**
 * SpringEL을 사용하기 위한 커스터마이징된 파서 객체.
 */
class CustomSpringELParser {

  companion object {
    fun getDynamicValue(parameterNames: Array<String>, args: Array<Any>, key: String): Any {
      val parser: ExpressionParser = SpelExpressionParser()
      val context = StandardEvaluationContext()

      for (i in parameterNames.indices) {
        context.setVariable(parameterNames[i], args[i])
      }

      return parser.parseExpression(key)
        .getValue(context, Any::class)!!
    }
  }
}
