package pirene.compiler

import cats.*
import cats.mtl.*
import cats.implicits.*
import higherkindness.droste.Algebra
import higherkindness.droste.scheme
import io.circe.*
import pirene.ast.*
import pirene.util.PathIdent

class CompilerImpl[F[_]](using Monad[F], Raise[F, CompileError])
    extends CompilerAlgebra[F] {

  override type Value = Json

  override type Decode[A] = Decoder[A]

  override type Encode[A] = Encoder[A]

  override def compile(
      ctx: Context[F, Value],
      expr: Expr
  ): ValueProgram = ???
  // scheme.cata(CompilerImpl.generationAlgebra[F])

  override def compileStatic[A, B](ctx: Context[F, Value], expr: Expr)(using
      Encode[A],
      Decode[B]
  ): Program[A, B] = ???

}
object CompilerImpl {

  def constantToJson(c: Constant): Json = c match {
    case c: String  => Json.fromString(c)
    case c: Boolean => Json.fromBoolean(c)
    case c: Long    => Json.fromLong(c)
    case c: Double  => Json.fromDoubleOrNull(c)
    case c: Unit    => Json.obj()
  }

  def generationAlgebra[F[_]](using F: Monad[F], FR: Raise[F, CompileError]) = {
    type Ctx = Context[F, Json]

    Algebra[[A] =>> (Ctx, ExprF[A]), List[Json] => F[Json]] {
      case (_, ExprF.Const(c)) =>
        _ => constantToJson(c).pure
      case (_, ExprF.Bind(_, f)) => f
      case (_, ExprF.Apply(applied, args)) =>
        _ => args.traverse(arg => arg(Nil)).flatMap(applied)
      case (_, ExprF.Lambda(_, out)) => out
      case (ctx, ExprF.Ref(ref)) =>
        Function.const {
          ctx
            .defIdent(ref)
            .fold(FR.raise(CompileError.NotFoundValue(ref)))(ctx.getDef(_)(Nil))
        }
    }
  }

}
