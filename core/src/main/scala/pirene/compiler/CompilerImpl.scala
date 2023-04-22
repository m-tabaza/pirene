package pirene.compiler

import cats.*
import cats.syntax.all.*
import higherkindness.droste.Algebra
import higherkindness.droste.scheme
import io.circe.*
import pirene.ast.*
import pirene.util.*

import pirene.util.PathIdent
import pirene.ast.{Expr, ExprF}
class CompilerImpl[F[_]](using MonadError[F, CompileError])
    extends CompilerAlgebra[F] {

  override type Value = Json

  override def compile(ctx: Context[ValueProgram], expr: Expr): ValueProgram =
    CompilerImpl.compile(ctx, expr)

}
object CompilerImpl {

  type ValueProgram[F[_]] = List[Json] => F[Json]

  def compile[F[_]](ctx: Context[ValueProgram[F]], expr: Expr)(using
      F: MonadError[F, CompileError]
  ): ValueProgram[F] = scheme.cata(CompilerImpl.compileAlg[F]).apply(expr)(ctx)

  def compileAlg[F[_]](using F: MonadError[F, CompileError]) =
    Algebra[ExprF, Context[ValueProgram[F]] => ValueProgram[F]] {
      case ExprF.Const(c) => _ => _ => c.pure
      case ExprF.Bind(ident, term, in) =>
        ctx => in(Context.withDef(ctx, PathIdent.from(ident), term(ctx)))
      case ExprF.Apply(applied, args) =>
        ctx => _ => args.traverse(_.apply(ctx)(Nil)).flatMap(applied(ctx)(_))
      case ExprF.Ref(ref) =>
        ctx => { args =>
          Context
            .defIdent(ctx, ref)
            .fold(F.raiseError(CompileError.NotFoundValue(ref))) { ident =>
              Context.getDef(ctx)(ident)(args)
            }
        }
      case ExprF.Lambda(params, expr) =>
        ctx => { args =>
          val innerCtx =
            params.zip(args).foldLeft(ctx) { case (accCtx, (param, value)) =>
              Context
                .withDef(accCtx, PathIdent.from(param), _ => value.pure)
            }

          expr(innerCtx)(args)
        }
    }

}
