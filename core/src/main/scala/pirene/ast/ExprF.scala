package pirene.ast

import cats.*
import cats.derived.{semiauto => derived, _}
import pirene.util.PathIdent
import pirene.util.Ident

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
