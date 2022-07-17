package pirene.compiler

import pirene.ast.Context

import pirene.ast.Expr
import io.circe.Json
import io.circe.Decoder
import higherkindness.droste.Algebra
import pirene.ast.ExprF
import io.circe.Encoder

class CompilerImpl[F[_]] extends CompilerAlgebra[F] {

  override type Value = Json

  override type Decode[A] = Decoder[A]

  override type Encode[A] = Encoder[A]

  override def compile(ctx: Context[F], expr: Expr): F[List[Value] => Value] =
    ???

  override def compileStatic[A, B](ctx: Context[F], expr: Expr)(using
      Encode[A],
      Decode[B]
  ): F[A => B] =
    ???

}
object CompilerImpl {

  def generationAlgebra[F[_]] = Algebra[ExprF, F[Nothing]]

}
