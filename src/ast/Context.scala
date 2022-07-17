package pirene.ast

import cats.syntax.all.*

import pirene.util.PathIdent
import pirene.compiler.CompilerAlgebra

class Context[F[_]](val compiler: CompilerAlgebra[F])(
    defs: Map[PathIdent, List[compiler.Value] => F[compiler.Value]]
) {

  opaque final type DefIdent = PathIdent

  def defIdent(ident: PathIdent): Option[DefIdent] =
    defs.get(ident).as(ident)

  def getDef(ident: DefIdent) = defs(ident)

  def withDef(
      ident: PathIdent,
      value: List[compiler.Value] => F[compiler.Value]
  ): Context[F] = Context(compiler)(defs.updated(ident, value))

}
