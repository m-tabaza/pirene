package pirene.compiler

import cats.*
import cats.data.EitherT
import cats.implicits.*
import cats.mtl.Handle
import cats.mtl.implicits.*
import higherkindness.droste.*
import io.circe.Json
import pirene.ast.*
import pirene.util.*

class CompilerImplTest extends munit.FunSuite {

  def dummyPrelude[F[_]](using F: Monad[F], RunErr: Handle[F, Throwable]) =
    Context.of[List[Json] => F[Json]](
      PathIdent.from("hello") -> { _ =>
        Json.fromString("Hello").pure
      },
      PathIdent.from("add") -> { args =>
        (args.get(0), args.get(1)).bisequence match {
          case Some((aJson, bJson)) => {
            val res = for {
              a <- aJson.as[Long]
              b <- bJson.as[Long]
            } yield Json.fromLong(a + b)

            res.fold(RunErr.raise, F.pure)
          }
          case None =>
            RunErr.raise(Exception("There should be two arguments"))
        }
      }
    )

  // val defaultCtx = dummyPrelude[ErrorOr]

  // val compiler = CompilerImpl[ErrorOr]

  // test("Constant program should be generated correctly") {

  //   /** TODO: In order to compile, there must be a function that turns Exprs
  //     * into Exprs with Contexts
  //     */
  //   val one = Expr.const(1L)

  // }

}
