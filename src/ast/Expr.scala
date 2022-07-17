package pirene.ast

import cats.Foldable
import cats.Functor
import cats.Traverse
import cats.derived.{semiauto => derived, _}
import higherkindness.droste.data.Fix
import pirene.util.Ident
import pirene.util.PathIdent

type Expr = Fix[ExprF]
object Expr {

  case class Param(ident: Ident, tpe: Type)

}

enum ExprF[A] {
  case Const(value: Constant)
  case Bind(ident: Ident, term: A)
  case Apply(applied: A, args: List[A])
  case Lambda(params: List[Expr.Param], body: A)
  case Ref(ref: PathIdent)
}

object ExprF {

  given Functor[ExprF] = derived.functor

  given Foldable[ExprF] = derived.foldable

  given Traverse[ExprF] = derived.traverse

}
