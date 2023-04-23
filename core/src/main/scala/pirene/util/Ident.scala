package pirene.util

import cats.Show

opaque type Ident = String
object Ident {

  def from(s: String): Ident = s

  def toString(ident: Ident): String = ident

  given Show[Ident] = Show.show(toString)

}
