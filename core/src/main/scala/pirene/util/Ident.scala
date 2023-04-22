package pirene.util

opaque type Ident = String
object Ident {

  def from(s: String): Ident = s

  def toString(ident: Ident): String = ident

}
