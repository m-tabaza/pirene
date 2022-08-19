package pirene.ast

import cats.*
import cats.derived.{semiauto => derived, _}
import higherkindness.droste.data.Fix
import pirene.util.Ident
import pirene.util.PathIdent

type Expr = Fix[ExprF]

object Expr {

  def const(c: Constant): Expr = Fix(ExprF.Const(c))

  def bind(ident: Ident, term: Expr, in: Expr): Expr =
    Fix(ExprF.Bind(ident, term, in))

  def ap(applied: Expr, args: List[Expr]): Expr =
    Fix(ExprF.Apply(applied, args))

  def ref(path: PathIdent): Expr = Fix(ExprF.Ref(path))

  def lambda(body: Expr, params: Ident*): Expr =
    Fix(ExprF.Lambda(params.toList, body))

}

type Constant = io.circe.Json

enum ExprF[A] {
  case Const(value: Constant)
  case Bind(ident: Ident, term: A, in: A)
  case Apply(applied: A, args: List[A])
  case Lambda(params: List[Ident], body: A)
  case Ref(ref: PathIdent)
}

object ExprF {

  given Functor[ExprF] = derived.functor

  given Foldable[ExprF] = derived.foldable

  given Traverse[ExprF] = derived.traverse

}
