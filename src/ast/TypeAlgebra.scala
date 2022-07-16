package pirene.ast

import cats.Eval
import cats.data.NonEmptyList
import cats.data.OneAnd
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.Size
import eu.timepit.refined.numeric.Positive

trait TypeAlgebra {
  import TypeAlgebra.*

  type Repr

  type Type[_ <: AnyKind]

  type Bound

  def repr[A <: AnyKind](using Type[A]): Repr

  /** @return `a =:= b` */
  def equate(a: Repr, b: Repr): Boolean

  /** @return `a <:< b` */
  def subtype(a: Repr, b: Repr): Boolean

  def tuple(elems: OneAnd[NonEmptyList, Repr]): Repr

  final def tuple(first: Repr, second: Repr, rest: Repr*): Repr =
    tuple(OneAnd(first, NonEmptyList(second, rest.toList)))

  def union(elems: NonEmptyList[Repr]): Repr

  final def union(head: Repr, tail: Repr*): Repr =
    union(NonEmptyList(head, tail.toList))

  def record(fields: NonEmptyList[(String, Repr)]): Repr

  final def record(head: (String, Repr), tail: (String, Repr)*): Repr =
    record(NonEmptyList(head, tail.toList))

  def applicable(t: Repr): Option[Repr Refined Applicable[Int Refined Positive]]

  def function(in: Repr, out: Repr): Repr

  final def curriedFunction(types: OneAnd[NonEmptyList, Repr]): Repr =
    function(
      types.head,
      types.tail.reverse.reduceLeft((acc, t) => function(t, acc))
    )

  final def curriedFunction(in1: Repr, in2: Repr, rest: Repr*): Repr =
    curriedFunction(OneAnd(in1, NonEmptyList(in2, rest.toList)))

  def applied[N <: Int Refined Positive](
      t: Repr Refined Applicable[N],
      args: NonEmptyList[Repr] Refined Size[N]
  ): Either[IncompatibleBoundsError[Repr, Bound], Repr]

}
object TypeAlgebra {

  case class Applicable[N <: Int Refined Positive](kind: N)

  case class IncompatibleBoundsError[Repr, Bound](
      incompatibleArgIndex: Int,
      incompatibleArg: Repr,
      unsatisfiedBound: Bound
  )

}
