package pirene.compiler

import cats.*
import cats.data.EitherT
import cats.implicits.*
import io.circe.Json
import pirene.ast.*
import pirene.util.*

class CompilerImplTest extends munit.FunSuite {

  def dummyPrelude[F[_]](using F: MonadError[F, Throwable]) =
    Context.of[List[Json] => F[Json]](
      PathIdent.from("add") -> { args =>
        (args.get(0), args.get(1)).bisequence match {
          case Some((aJson, bJson)) => {
            val res = for {
              a <- aJson.as[Long]
              b <- bJson.as[Long]
            } yield Json.fromLong(a + b)

            res.fold(F.raiseError, F.pure)
          }
          case None =>
            F.raiseError(Exception("There should be two arguments"))
        }
      }
    )

  type ErrorOr[A] = EitherT[[X] =>> EitherT[Id, Throwable, X], CompileError, A]

  def ensure[A](fa: ErrorOr[A])(err: => Nothing)(p: A => Boolean): Unit =
    fa.ensure(err)(p)
      .recover(_ => err)
      .value
      .recover(_ => err)
      .map(_.recover(_ => err))

  test("Constant program generation") {
    val one = Expr.const(1L)

    val program = CompilerImpl.compile(dummyPrelude[ErrorOr], one)

    ensure(program(Nil))(fail("Result should be 1"))(_ == Json.fromLong(1L))
  }

  test("Referenced function application generation") {
    val app = Expr.ap(
      applied = Expr.ref(PathIdent.from("add")),
      args = List(Expr.const(2L), Expr.const(3L))
    )

    val program = CompilerImpl.compile(dummyPrelude[ErrorOr], app)

    ensure(program(Nil))(fail("Result should be 5"))(_ == Json.fromLong(5L))
  }

  test("Lambda application generation") {
    val lambda = Expr.lambda(
      body = Expr.ap(
        applied = Expr.ref(PathIdent.from("add")),
        args =
          List(Expr.ref(PathIdent.from("x")), Expr.ref(PathIdent.from("y")))
      ),
      Expr.Param(Ident.from("x"), Type.int),
      Expr.Param(Ident.from("y"), Type.int)
    )

    val program = CompilerImpl.compile(dummyPrelude[ErrorOr], lambda)

    ensure(program(Json.fromLong(3L) :: Json.fromLong(2L) :: Nil)) {
      fail("Result should be 5")
    }(_ == Json.fromLong(5L))
  }

  test("Bindings generation") {
    val bind = Expr.bind(
      ident = Ident.from("x"),
      term = Expr.const(1L),
      in = Expr.bind(
        ident = Ident.from("y"),
        term = Expr.const(2L),
        in = Expr.ap(
          applied = Expr.ref(PathIdent.from("add")),
          args =
            List(Expr.ref(PathIdent.from("x")), Expr.ref(PathIdent.from("y")))
        )
      )
    )

    val program = CompilerImpl.compile(dummyPrelude[ErrorOr], bind)

    ensure(program(Nil))(fail("Result should be 3"))(_ == Json.fromLong(3L))
  }

}
