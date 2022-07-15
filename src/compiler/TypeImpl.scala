package pirene.compiler

import scala.quoted.*
import pirene.ast.TypeAlgebra
import cats.data.OneAnd
import cats.data.NonEmptyList
import cats.Eval

class TypeImpl[F[_]](using val quotes: Quotes) extends TypeAlgebra[F] {
  import quotes.reflect.*

  type Repr = TypeRepr

  type Type[A] = quoted.Type[A]

  override def repr[A](using Type[A]): Repr = TypeRepr.of[A]

  override def equate(a: Repr, b: Repr): Boolean = a =:= b

  override def subtype(a: Repr, b: Repr): Boolean = a <:< b

  override def tuple(elems: OneAnd[NonEmptyList, Repr]): Repr =
    elems
      .foldRight(Eval.now(TypeRepr.of[EmptyTuple])) { (acc, et) =>
        Eval.later(TypeRepr.of[*:].appliedTo(acc :: et.value :: Nil))
      }
      .value

  override def record(fields: NonEmptyList[(String, Repr)]): Repr =
    fields.tail.foldLeft {
      val NonEmptyList((headIdent, headRepr), _) = fields

      Refinement(
        TypeRepr.of[Object],
        headIdent,
        headRepr
      )
    } { case (parent, (ident, repr)) =>
      Refinement(parent, ident, repr)
    }

  override def union(elems: NonEmptyList[Repr]): Repr =
    elems.reduceLeft((acc, t) => OrType(t, acc))

}
