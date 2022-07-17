package pirene.compiler

import pirene.ast.{Expr, Context}

trait CompilerAlgebra[F[_]] {

  type Value

  type Decode[_]

  type Encode[_]

  def compile(ctx: Context[F], expr: Expr): F[List[Value] => Value]

  def compileStatic[A, B](ctx: Context[F], expr: Expr)(using
      Encode[A],
      Decode[B]
  ): F[A => B]

}
