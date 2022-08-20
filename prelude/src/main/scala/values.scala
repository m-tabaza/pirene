package pirene.prelude

inline def funcDef[A](ident: String)(impl: A)(using ta: TypeOf[A]) =
  (ident, impl, ta.get)

inline def values[F[_]] = List(
  funcDef("add") { (x: Int) => (y: Int) => x + y },
  funcDef("multiply") { (x: Int) => (y: Int) => x * y }
)
