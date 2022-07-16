package pirene.compiler

import pirene.ast.{Expr, Context}

trait CompilerAlgebra[F[_]] {

  type Value

  type Decode[_]

  def compile(ctx: Context, expr: Expr): F[Value]

  def compileA[A](ctx: Context, expr: Expr)(using Decode[A]): F[A]

}
