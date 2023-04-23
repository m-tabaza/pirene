package pirene.compiler

import cats.MonadError
import cats.implicits.*
import higherkindness.droste.Algebra
import pirene.ast.ExprF
import pirene.util.PathIdent

object CompileAlgebra {

  def apply[F[_], Value](using F: MonadError[F, CompileError[Value]]) = {
    type Acc = Context[Program[F, Value]] => Program[F, Value]

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
              Context.withDef(accCtx, PathIdent.from(param), _ => value.pure)
            }

          expr(innerCtx)(args)
        }
    }
  }

}
