package pirene.ast

import cats.*
import cats.data.NonEmptyList
import cats.data.OneAnd
import cats.derived.{semiauto => derived, _}
import pirene.util.PathIdent
import higherkindness.droste.data.Fix

import pirene.util.PathIdent
type Type = Fix[TypeF]
object Type {

  case class Param(ident: PathIdent, kind: scala.Int)

  val unit: Type = Fix(TypeF.Unit())

  val string: Type = Fix(TypeF.String())

  val int: Type = Fix(TypeF.Int())

  val byte: Type = Fix(TypeF.Byte())

  val float: Type = Fix(TypeF.Float())

  val bool: Type = Fix(TypeF.Boolean())

  def function(in: Type, out: Type): Type =
    Fix(TypeF.Function(in, out))

  /** @return function type `a => b => ...` */
  def curriedFunction(a: Type, b: Type, rest: Type*): Type =
    function(
      a,
      rest.toList match {
        case Nil     => b
        case x :: xs => curriedFunction(b, x, xs: _*)
      }
    )

  def product(a: Type, b: Type, rest: Type*) =
    Fix(TypeF.Product(OneAnd(a, NonEmptyList(b, rest.toList))))

  def record(head: (PathIdent, Type), tail: (PathIdent, Type)*): Type =
    Fix(TypeF.Record(NonEmptyList(head, tail.toList)))

  def coproduct(head: Type, tail: Type*): Type =
    Fix(TypeF.Coproduct(NonEmptyList(head, tail.toList)))

  def ap(applied: Type, args: NonEmptyList[Type]): Type =
    Fix(TypeF.Apply(applied, args))

  def ap(applied: Type, arg: Type, rest: Type*): Type =
    ap(applied, NonEmptyList(arg, rest.toList))

  def lambda(body: Type, paramsHead: Param, paramsTail: Param*): Type =
    Fix(TypeF.Lambda(NonEmptyList(paramsHead, paramsTail.toList), body))

  def ref(path: PathIdent): Type =
    Fix(TypeF.Ref(path))

}

enum TypeF[A] {

  /** `Unit` */
  case Unit()

  /** `String` */
  case String()

  /** `Long` */
  case Int()

  /** `Byte` */
  case Byte()

  /** `Double` */
  case Float()

  /** `Boolean` */
  case Boolean()

  /** `(String, Int, ...)` */
  case Product(members: OneAnd[NonEmptyList, A])

  /** `(a: Int, b: Int, ...)` */
  case Record(members: NonEmptyList[(PathIdent, A)])

  /** `(Int, String, ...) | (String, Float) | MyProduct | Int` */
  case Coproduct(variants: NonEmptyList[A])

  /** `A => B` or `A => (B => (C => D))` */
  case Function(in: A, out: A)

  /** `T[...args]` or `T` */
  case Apply(applied: A, args: NonEmptyList[A])

  /** `[X] =>> F[X]` */
  case Lambda(in: NonEmptyList[Type.Param], out: A)

  /** `Option`, `Array` */
  case Ref(path: PathIdent)

}
object TypeF {

  given Functor[TypeF] = derived.functor

  given Foldable[TypeF] = derived.foldable

  given Traverse[TypeF] = derived.traverse

}
