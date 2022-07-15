package pirene.ast

import cats.data.{OneAnd, NonEmptyList}

trait TypeAlgebra[F[_]] {

  type Repr

  type Type[_]

  def repr[A](using Type[A]): Repr

  /** @return `a =:= b` */
  def equate(a: Repr, b: Repr): Boolean

  /** @return `a <:< b` */
  def subtype(a: Repr, b: Repr): Boolean

  def tuple(elems: OneAnd[NonEmptyList, Repr]): Repr

  def tuple(first: Repr, second: Repr, rest: Repr*): Repr =
    tuple(OneAnd(first, NonEmptyList(second, rest.toList)))

  def union(elems: NonEmptyList[Repr]): Repr

  def union(head: Repr, tail: Repr*): Repr =
    union(NonEmptyList(head, tail.toList))

  def record(fields: NonEmptyList[(String, Repr)]): Repr

  def record(head: (String, Repr), tail: (String, Repr)*): Repr =
    record(NonEmptyList(head, tail.toList))

}
