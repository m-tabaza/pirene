package pirene.ast

import higherkindness.droste.Algebra
import higherkindness.droste.data.Fix
import higherkindness.droste.scheme
import pirene.util.*

type Expr[C] = Fix[[A] =>> ExprF[A, C]]

given Functor[Expr] with {

  override def map[A, B](fa: Expr[A])(f: A => B): Expr[B] =
    scheme.cata(Expr.mapAlg(f)).apply(fa)

}

object Expr {

  def const[C](c: C): Expr[C] = Fix(ExprF.Const(c))

  def bind[C](ident: Ident, term: Expr[C], in: Expr[C]): Expr[C] =
    Fix(ExprF.Bind(ident, term, in))

  def ap[C](applied: Expr[C], args: List[Expr[C]]): Expr[C] =
    Fix(ExprF.Apply(applied, args))

  def ref[C](path: PathIdent): Expr[C] = Fix(ExprF.Ref(path))

  def lambda[C](body: Expr[C], params: Ident*): Expr[C] =
    Fix(ExprF.Lambda(params.toList, body))

  def mapAlg[A, B](f: A => B) = Algebra[[R] =>> ExprF[R, A], Expr[B]] {
    case ExprF.Const(value)          => Fix(ExprF.Const(f(value)))
    case ExprF.Apply(applied, args)  => Fix(ExprF.Apply(applied, args))
    case ExprF.Bind(ident, term, in) => Fix(ExprF.Bind(ident, term, in))
    case ExprF.Lambda(params, body)  => Fix(ExprF.Lambda(params, body))
    case ExprF.Ref(ref)              => Fix(ExprF.Ref(ref))
  }

}
