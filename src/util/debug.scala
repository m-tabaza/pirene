package pirene.util

import quoted.*

inline def debug[A](inline a: A): A = ${ debugCode('a) }

private def debugCode[A: Type](a: Expr[A])(using Quotes): Expr[A] = {
  import quotes.reflect.*

  val term = a.asTerm
  val sym = term.symbol
  val name = a.asTerm match {
    case Inlined(_, _, Ident(ident)) => Expr(ident)
    case _                           => Expr(sym.fullName)
  }
  val tpe = Expr(Type.show[A])
  val pos = Position.ofMacroExpansion
  val posStr = Expr {
    val path = pos.sourceFile.getJPath.map(_.toString).getOrElse("")
    s"$path:${pos.startLine + 1}:${pos.startColumn + 1}"
  }

  '{
    val aVal = $a
    println(
      s"${$name} - (${compiletime.codeOf($a)}) : ${$tpe} = $aVal @ ${$posStr}"
    )
    aVal
  }
}
