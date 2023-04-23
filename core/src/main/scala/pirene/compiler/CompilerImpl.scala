package pirene.compiler

import cats.*
import higherkindness.droste.scheme
import pirene.ast.*
import pirene.ast.{Expr}
import pirene.ast.ExprF

class CompilerImpl[F[_], V](using MonadError[F, CompileError[V]])
    extends Compiler[F] {

  override type Value = V

  override def compile(
      ctx: Context[ValueProgram],
      expr: Expr[Value]
  ): ValueProgram = CompilerImpl.compile(ctx, expr)

}
object CompilerImpl {

  def compile[F[_], Value](
      ctx: Context[Program[F, Value]],
      expr: Expr[Value]
  )(using
      F: MonadError[F, CompileError[Value]]
  ): Program[F, Value] =
    scheme.cata(CompileAlgebra[F, Value]).apply(expr)(ctx)

}
