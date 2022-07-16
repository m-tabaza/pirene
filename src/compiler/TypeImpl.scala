package pirene.compiler

import cats.Eval
import cats.data.NonEmptyList
import cats.data.OneAnd
import eu.timepit.refined.api.Refined as R
import eu.timepit.refined.collection.Size
import eu.timepit.refined.numeric.Positive
import pirene.ast.TypeAlgebra
import pirene.ast.TypeAlgebra.*

import scala.quoted.*

class TypeImpl(using val quotes: Quotes) extends TypeAlgebra {
  import quotes.reflect.*

  type Repr = TypeRepr

  type Type[A <: AnyKind] = quoted.Type[A]

  type Bound = TypeBounds

  override def repr[A <: AnyKind](using Type[A]): Repr = TypeRepr.of[A]

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

  override def function(in: Repr, out: Repr): Repr =
    TypeRepr.of[Function].appliedTo(in :: out :: Nil)

  override def applicable(t: Repr): Option[Repr R Applicable[Int R Positive]] =
    t.dealias match {
      case TypeLambda(params, _, _) => Some(R.unsafeApply(t))
      case _                        => None
    }

  override def applied[N <: Int R Positive](
      t: Repr R Applicable[N],
      args: NonEmptyList[Repr] R Size[N]
  ): Either[IncompatibleBoundsError[Repr, Bound], Repr] = t.value match {
    case TypeLambda(_, bounds, _) =>
      (bounds zip args.value.toList).zipWithIndex
        .find { case ((bounds, arg), _) => !(arg <:< bounds) } match {
        case Some(((unsatisfiedBound, badArg), index)) =>
          Left(IncompatibleBoundsError(index, badArg, unsatisfiedBound))
        case None => Right(t.value appliedTo args.value.toList)
      }
  }

}
