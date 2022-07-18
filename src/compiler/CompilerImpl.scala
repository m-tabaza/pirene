package pirene.compiler

import pirene.ast.Context

import pirene.ast.Expr
import io.circe.Json
import io.circe.Decoder
import higherkindness.droste.RAlgebra
import pirene.ast.ExprF
import io.circe.Encoder
import cats.implicits.*
import higherkindness.droste.Algebra
import pirene.util.PathIdent
import cats.Monad

class CompilerImpl[F[_]] extends CompilerAlgebra[F] {

  override type Value = Json

  override type Decode[A] = Decoder[A]

  override type Encode[A] = Encoder[A]

  override def compile(
      ctx: Context[F, Value],
      expr: Expr
  ): List[Value] => F[Value] =
    ???

  override def compileStatic[A, B](ctx: Context[F, Value], expr: Expr)(using
      Encode[A],
      Decode[B]
  ): A => F[B] =
    ???

}
object CompilerImpl {

  def generationAlgebra[F[_]](prelude: Context[F, Json])(using Monad[F]) =
    Algebra[ExprF, (Context[F, Json], List[Json] => F[Json])] {
      case ExprF.Const(c: String) =>
        (prelude, _ => Json.fromString((c)).pure)
      case ExprF.Const(c: Long) =>
        (prelude, _ => Json.fromLong((c)).pure)
      case ExprF.Const(c: Boolean) =>
        (prelude, _ => Json.fromBoolean((c)).pure)
      case ExprF.Const(c: Double) =>
        (prelude, _ => Json.fromDoubleOrNull(c).pure)
      case ExprF.Const(c: Unit) =>
        (prelude, _ => Json.obj().pure)
      case ExprF.Bind(ident, (ctx, f)) =>
        (ctx.withDef(PathIdent.from(ident), f), f)
      case ExprF.Apply((appliedCtx, applied), args) =>
        appliedCtx -> { _ =>
          args.traverse { case (_, arg) => arg(Nil) }.flatMap(applied)
        }
      case ExprF.Lambda(params, out) => ???
      case ExprF.Ref(_)              => ???
    }

}
