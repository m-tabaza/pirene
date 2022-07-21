package pirene.compiler

import pirene.ast.Expr

trait CompilerAlgebra[F[_]] {

  type Value

  type Program[-I, O] = List[I] => F[O]

  type ValueProgram = Program[Value, Value]

  type Decode[_]

  type Encode[_]

  def compile(ctx: Context[F, Value], expr: Expr): ValueProgram

  def compileStatic[A, B](ctx: Context[F, Value], expr: Expr)(using
      Encode[A],
      Decode[B]
  ): Program[A, B]

}
