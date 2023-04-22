package pirene.compiler

import pirene.ast.Expr

import pirene.ast.Expr
trait CompilerAlgebra[F[_]] {

  type Value

  type Program[-I, O] = List[I] => F[O]

  type ValueProgram = Program[Value, Value]

  def compile(ctx: Context[ValueProgram], expr: Expr): ValueProgram

}
