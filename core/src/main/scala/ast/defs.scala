package pirene.ast

import pirene.util.PathIdent

case class FunctionDef(
    typeParams: List[Type.Param],
    termParams: List[(PathIdent, Type)],
    out: Type,
    body: Expr
)
