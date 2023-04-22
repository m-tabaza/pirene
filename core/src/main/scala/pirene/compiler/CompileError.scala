package pirene.compiler

import pirene.ast.*
import pirene.util.PathIdent

import pirene.util.PathIdent
import pirene.ast.Type
import pirene.ast.Expr
enum CompileError extends Exception {
  case NotFoundType(ident: PathIdent)
  case NotFoundValue(ident: PathIdent)
  case TypeMismatch(foundExpr: Expr, foundType: Type, requiredType: Type)
}
