package pirene.compiler

import pirene.util.PathIdent

import pirene.ast.Type
import pirene.ast.Expr

enum CompileError[C] extends Exception {
  case NotFoundType(ident: PathIdent)
  case NotFoundValue(ident: PathIdent)
  case TypeMismatch(foundExpr: Expr[C], foundType: Type, requiredType: Type)
}
