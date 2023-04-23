package pirene.ast.json

import pirene.ast.Expr
import pirene.util.Ident
import pirene.util.PathIdent

class ExprSerializationTest extends munit.FunSuite {

  val expr = Expr.bind(
    Ident.from("y"),
    Expr.ap(
      Expr.lambda(Expr.ref(PathIdent.from("x")), Ident.from("x")),
      List(Expr.const(1))
    ),
    Expr.const(2)
  )

  val json = ExprEncoder.encode(expr)

  val decoded = ExprDecoder
    .decode[Int](json)
    .value
    .value
    .getOrElse(fail("Decoding should not fail"))

  assertEquals(expr, decoded)

}
