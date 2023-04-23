package pirene.compiler

import cats.*
import cats.syntax.all.*
import higherkindness.droste.Algebra
import higherkindness.droste.scheme
import pirene.ast.*
import pirene.util.*

import pirene.util.PathIdent
import pirene.ast.{Expr, ExprF}

class CompilerImpl[F[_], V](using MonadError[F, CompileError[V]])
    extends CompilerAlgebra[F] {

  override type Value = V

  override def compile(
      ctx: Context[ValueProgram],
      expr: Expr[Value]
  ): ValueProgram = CompilerImpl.compile(ctx, expr)

}
object CompilerImpl {

  type ValueProgram[F[_], Value] = List[Value] => F[Value]

  def compile[F[_], Value](
      ctx: Context[ValueProgram[F, Value]],
      expr: Expr[Value]
  )(using
      F: MonadError[F, CompileError[Value]]
  ): ValueProgram[F, Value] =
    scheme.cata(CompilerImpl.compileAlg[F, Value]).apply(expr)(ctx)

  def compileAlg[F[_], Value](using F: MonadError[F, CompileError[Value]]) = {
    type Acc = Context[ValueProgram[F, Value]] => ValueProgram[F, Value]

    Algebra[[A] =>> ExprF[A, Value], Acc] {
      case ExprF.Const(c) => _ => _ => c.pure
      case ExprF.Bind(ident, term, in) =>
        ctx => in(Context.withDef(ctx, PathIdent.from(ident), term(ctx)))
      case ExprF.Apply(applied, args) =>
        ctx => _ => args.traverse(_.apply(ctx)(Nil)).flatMap(applied(ctx))
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

}
