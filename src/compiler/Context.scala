package pirene.compiler

import pirene.util.PathIdent

class Context[F[_], Value](val defs: Map[PathIdent, List[Value] => F[Value]]) {

  opaque final type DefIdent = PathIdent

  def defIdent(ident: PathIdent): Option[DefIdent] =
    defs.get(ident).map(_ => ident)

  def getDef(ident: DefIdent) = defs(ident)

  def withDef(
      ident: PathIdent,
      value: List[Value] => F[Value]
  ): Context[F, Value] = Context(defs.updated(ident, value))

  def merge(that: Context[F, Value]): Context[F, Value] =
    Context(defs ++ that.defs)

}
object Context {

  def empty[F[_], Value]: Context[F, Value] = Context(Map.empty)

  def of[F[_], Value](
      defs: (PathIdent, List[Value] => F[Value])*
  ): Context[F, Value] = Context(defs.toMap)

}
