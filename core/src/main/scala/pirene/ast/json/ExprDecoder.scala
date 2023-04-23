package pirene.ast.json

import cats.Eval
import cats.data.EitherT
import cats.implicits.*
import io.circe.Decoder
import io.circe.DecodingFailure
import io.circe.Json
import pirene.ast.Expr
import pirene.util.Ident
import pirene.util.PathIdent

object ExprDecoder {

  def decode[C](
      json: Json
  )(using Decoder[C]): EitherT[Eval, DecodingFailure, Expr[C]] = for {
    obj <- EitherT.fromEither { json.as[Map[String, Json]] }
    pair <- EitherT.fromOption(
      obj.toList.headOption,
      DecodingFailure("Node object must contain one key", Nil)
    )
    (typeStr, value) = pair
    node <- typeStr match {
      case "const" => EitherT.fromEither[Eval](value.as[C].map(Expr.const))

      case "ref" =>
        EitherT.fromEither[Eval] {
          value.as[String].flatMap { s =>
            Either.fromOption(
              PathIdent.parse(s).map(Expr.ref),
              DecodingFailure(s"Invalid path identifier `$s`", Nil)
            )
          }
        }

      case "apply" =>
        for {
          applied <- EitherT
            .fromEither {
              value.hcursor
                .downField("applied")
                .as[Json]
            }
            .flatMap(decode)

          args <- EitherT
            .fromEither {
              value.hcursor
                .downField("args")
                .as[List[Json]]
            }
            .flatMap(_.traverse(decode))
        } yield Expr.ap(applied, args)

      case "bind" =>
        for {
          ident <- EitherT.fromEither[Eval] {
            value.hcursor
              .downField("ident")
              .as[String]
              .map(s => Ident.from(s.trim))
          }
          term <- EitherT
            .fromEither {
              value.hcursor.downField("term").as[Json]
            }
            .flatMap(decode)
          in <- EitherT
            .fromEither {
              value.hcursor.downField("in").as[Json]
            }
            .flatMap(decode)
        } yield Expr.bind(ident, term, in)

      case "lambda" =>
        for {
          body <- EitherT
            .fromEither {
              value.hcursor
                .downField("body")
                .as[Json]
            }
            .flatMap(decode)
          params <- EitherT.fromEither[Eval] {
            value.hcursor
              .downField("params")
              .as[List[String]]
              .nested
              .map(Ident.from)
              .value
          }
        } yield Expr.lambda(body, params: _*)
    }
  } yield node

}
