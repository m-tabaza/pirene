package pirene.prelude

import cats.data.NonEmptyList
import pirene.ast.Type

trait TypeOf[A] {
  def get: Type
}

object TypeOf {

  def apply[A](using typ: TypeOf[A]) = typ

  given TypeOf[Unit] with {
    inline def get = Type.unit
  }

  given TypeOf[Int] with {
    inline def get = Type.int
  }

  given TypeOf[Long] with {
    inline def get = Type.int
  }

  given TypeOf[String] with {
    inline def get = Type.string
  }

  given TypeOf[Boolean] with {
    inline def get = Type.bool
  }

  given [A, B](using ta: TypeOf[A], tb: TypeOf[B]): TypeOf[A => B] with {
    inline def get = Type.function(ta.get, tb.get)
  }

}

inline def funcDef[A](ident: String)(impl: A)(using ta: TypeOf[A]) =
  (ident, impl, ta.get)

inline def prelude[F[_]] = List(
  funcDef("add") { (x: Int) => (y: Int) => x + y },
  funcDef("mul") { (x: Int) => (y: Int) => x * y }
)
