package pirene.ast

import cats.syntax.all.*

import pirene.util.PathIdent

class Context(terms: Map[PathIdent, Expr], types: Map[PathIdent, Type]) {

  opaque type ExistingTermIdent = PathIdent
  opaque type ExistingTypeIdent = PathIdent

  def existingTermIdent(ident: PathIdent): Option[ExistingTermIdent] =
    terms.get(ident).as(ident)

  def existingTypeIdent(ident: PathIdent): Option[ExistingTypeIdent] =
    types.get(ident).as(ident)

  def getTerm(ident: ExistingTermIdent) = terms(ident)

  def getType(ident: ExistingTypeIdent) = types(ident)

  def addTerm(ident: PathIdent, term: Expr) =
    Context(terms.updated(ident, term), types)

  def addType(ident: PathIdent, typ: Type) =
    Context(terms, types.updated(ident, typ))

}
