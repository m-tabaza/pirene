package pirene.ast

import pirene.util.*

class ExprFunctorTest extends munit.FunSuite {

  val expr = Expr.bind(Ident.from("x"), Expr.const(1), Expr.const(2))

  val mapped = expr.map(_.toString)

  val expected = Expr.bind(Ident.from("x"), Expr.const("1"), Expr.const("2"))

  assertEquals(mapped, expected)

}
