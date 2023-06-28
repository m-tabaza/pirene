package pirene.stdlib

import cats.effect.IO

inline def funcDef[A](ident: String)(impl: A)(using ta: TypeOf[A]) =
  (ident, impl, ta.get)

inline def global[F[_]] = (
  funcDef("pi") { 22.0 / 7.0 },
  funcDef("+") { (x: Int) => (y: Int) => x + y },
  funcDef("*") { (x: Int) => (y: Int) => x * y },
  funcDef("println") { (s: String) => IO.println(s) }
)
