package pirene.compiler

import pirene.util.PathIdent

import pirene.util.PathIdent
opaque type Context[A] = Map[PathIdent, A]

opaque type DefIdent[Ctx] = PathIdent

object Context {

  def defIdent[A](
      ctx: Context[A],
      ident: PathIdent
  ): Option[DefIdent[ctx.type]] = ctx.get(ident).map(_ => ident)

  def getDef[A](ctx: Context[A])(ident: DefIdent[ctx.type]) = ctx(ident)

  def withDef[A](ctx: Context[A], ident: PathIdent, value: A): Context[A] =
    ctx.updated(ident, value)

  def merge[A](a: Context[A], b: Context[A]): Context[A] = a ++ b

  def empty[A]: Context[A] = Map.empty

  def of[A](defs: (PathIdent, A)*): Context[A] = defs.toMap

}
