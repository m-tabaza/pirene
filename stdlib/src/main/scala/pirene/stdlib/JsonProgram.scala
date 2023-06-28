package pirene.stdlib

import cats.Applicative
import cats.effect.kernel.Async
import cats.syntax.all.*
import io.circe.Codec
import io.circe.DecodingFailure
import io.circe.Json
import scala.quoted.Quotes
import scala.quoted.Expr as QExpr
import scala.quoted.Type as QType

object JsonProgram {

  trait JsonCodec[F[_], A] {

    def fromJson(json: Json): Either[DecodingFailure, A]

    def toJson(a: A): F[Json]

  }

  given [F[_]: Applicative, A: Codec]: JsonCodec[F, A] with {
    def fromJson(json: Json) = Codec[A].decodeJson(json)

    def toJson(a: A) = Codec[A].apply(a).pure
  }

  given [F[_]: Async, A: Codec]: JsonCodec[F, F[A]] with {
    def fromJson(json: Json) = Codec[A].decodeJson(json).map(_.pure)

    def toJson(fa: F[A]) = fa.map(Codec[A].apply)
  }

  transparent inline def from[F[_], P](
      primitive: P
  )(using TypeOf[P]) = ${ fromImpl('primitive) }

  def fromImpl[F[_], A](a: QExpr[A])(using QType[F], QType[A], Quotes) =
    a match {
      case '{ $f: Function[head, tail] } => {
        val codec = QExpr.summon[JsonCodec[F, head]].getOrElse {
          val typeStr = QExpr(QType.of[head].toString())
          val exprStr = QExpr(compiletime.codeOf(f))

          '{
            val msg =
              s"Cannot deserialize input of type ${$typeStr} in function: ${$exprStr}"

            compiletime.error(msg)
          }
        }

        ???

      }

      case '{ $other: a } =>
        QExpr.summon[JsonCodec[F, a]] match {
          case Some(codec) => '{ $codec.toJson($other) }
          case None => {
            val typeStr = QExpr(QType.of[a].toString())
            val exprStr = QExpr(compiletime.codeOf(other))
            '{
              val msg =
                s"Cannot serialize value of type ${$typeStr}: ${$exprStr}"
              compiletime.error(msg)
            }
          }
        }
    }

}
