package pirene.compiler

import pirene.ast.Expr

trait Compiler[F[_]] {

  type Value

  type ValueProgram = Program[F, Value]

  def compile(ctx: Context[ValueProgram], expr: Expr[Value]): ValueProgram

}
