package pirene.ast

import cats.Foldable
import cats.Functor
import cats.Traverse
import cats.derived.{semiauto => derived, _}
import pirene.util.PathIdent
import higherkindness.droste.data.Fix

type Expr = Fix[ExprF]
object Expr {

  case class Param(ident: PathIdent, tpe: Type)

}

enum ExprF[A] {
  case Const(value: Constant)
  case Bind(ident: PathIdent, term: A)
  case Apply(ident: Context#ExistingTermIdent, args: List[A])
  case Lambda(typeParams: List[Type.Param], params: List[Expr.Param], body: A)
}

object ExprF {

  given Functor[ExprF] = derived.functor

  given Foldable[ExprF] = derived.foldable

  given Traverse[ExprF] = derived.traverse

}
