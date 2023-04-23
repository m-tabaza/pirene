package pirene.ast.json

import cats.syntax.all.*
import higherkindness.droste.Algebra
import higherkindness.droste.scheme
import io.circe.Encoder
import io.circe.Json
import io.circe.syntax.*
import pirene.ast.Expr
import pirene.ast.ExprF

object ExprEncoder {

  def algebra[C](using Encoder[C]) = Algebra[[R] =>> ExprF[R, C], Json] {
    case ExprF.Const(c) => Json.obj("const" -> c.asJson)

    case ExprF.Apply(applied, args) =>
      Json.obj(
        "apply" -> Json.obj(
          "applied" -> applied,
          "args" -> args.asJson
        )
      )

    case ExprF.Bind(ident, term, in) =>
      Json.obj(
        "bind" -> Json.obj(
          "ident" -> ident.show.asJson,
          "term" -> term,
          "in" -> in
        )
      )

    case ExprF.Lambda(params, body) =>
      Json.obj(
        "lambda" -> Json.obj(
          "params" -> params.map(_.show.asJson).asJson,
          "body" -> body
        )
      )

    case ExprF.Ref(ref) =>
      Json.obj(
        "ref" -> ref.show.asJson
      )
  }

  def encode[C](expr: Expr[C])(using Encoder[C]): Json =
    scheme.cata(algebra[C]).apply(expr)

}
