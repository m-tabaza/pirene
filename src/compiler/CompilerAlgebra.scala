package pirene.compiler

import pirene.ast.{Expr, Context}

trait CompilerAlgebra[F[_]] {

  type Value

  type Decode[_]

  type Encode[_]

  def compile(ctx: Context[F, Value], expr: Expr): List[Value] => F[Value]

  def compileStatic[A, B](ctx: Context[F, Value], expr: Expr)(using
      Encode[A],
      Decode[B]
  ): A => F[B]

}
