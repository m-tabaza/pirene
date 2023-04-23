package pirene.ast

import cats.*
import cats.derived.{semiauto => derived, _}
import higherkindness.droste.data.Fix
import pirene.util.PathIdent
import pirene.util.Ident

type Expr[C] = Fix[[A] =>> ExprF[A, C]]

object Expr {

  def const[C](c: C): Expr[C] = Fix(ExprF.Const(c))

  def bind[C](ident: Ident, term: Expr[C], in: Expr[C]): Expr[C] =
    Fix(ExprF.Bind(ident, term, in))

  def ap[C](applied: Expr[C], args: List[Expr[C]]): Expr[C] =
    Fix(ExprF.Apply(applied, args))

  def ref[C](path: PathIdent): Expr[C] = Fix(ExprF.Ref(path))

  def lambda[C](body: Expr[C], params: Ident*): Expr[C] =
    Fix(ExprF.Lambda(params.toList, body))

}

enum ExprF[A, C] {
  case Const(value: C)
  case Bind(ident: Ident, term: A, in: A)
  case Apply(applied: A, args: List[A])
  case Lambda(params: List[Ident], body: A)
  case Ref(ref: PathIdent)
}

object ExprF {

  given [C]: Functor[[A] =>> ExprF[A, C]] = derived.functor

  given [C]: Foldable[[A] =>> ExprF[A, C]] = derived.foldable

  given [C]: Traverse[[A] =>> ExprF[A, C]] = derived.traverse

}
