package pirene.ast

import pirene.util.PathIdent

case class FunctionDef[C](
    typeParams: List[Type.Param],
    termParams: List[(PathIdent, Type)],
    out: Type,
    body: Expr[C]
)
