package pirene.compiler

import pirene.ast.*
import pirene.util.PathIdent

enum CompileError extends Exception {
  case NotFoundType(ident: PathIdent)
  case NotFoundValue(ident: PathIdent)
  case TypeMismatch(foundExpr: Expr, foundType: Type, requiredType: Type)
}