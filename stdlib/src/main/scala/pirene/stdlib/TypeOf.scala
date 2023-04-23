package pirene.stdlib

import pirene.ast.Type
import pirene.util.PathIdent

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

  given TypeOf[Float] with {
    inline def get = Type.float
  }

  given TypeOf[Double] with {
    inline def get = Type.float
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

  given [A](using ta: TypeOf[A]): TypeOf[Vector[A]] with {
    inline def get = Type.ap(Type.ref(PathIdent.from("Array")), ta.get)
  }

}
