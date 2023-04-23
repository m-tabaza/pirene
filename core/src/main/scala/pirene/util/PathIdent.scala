package pirene.util

import cats.Show
import cats.data.NonEmptyList
import cats.syntax.all.*

opaque type PathIdent = NonEmptyList[String]
object PathIdent {

  def from(head: String, tail: String*): PathIdent =
    NonEmptyList(head, tail.toList)

  def from(ident: Ident): PathIdent =
    PathIdent.from(Ident.toString(ident))

  def from(path: NonEmptyList[String]): PathIdent = path

  def toString(ident: PathIdent): String = ident.reduceLeft(_ + "." + _)

  def path(ident: PathIdent): NonEmptyList[String] = ident

  def parse(s: String): Option[PathIdent] = {
    val trimmed = s.trim
    if trimmed.isEmpty then None
    else {
      val fragments = s.trim.split(".")
      if fragments.isEmpty then from(trimmed).some
      else fragments.toList.toNel.nested.map(_.trim).value.map(from)
    }
  }

  given Show[PathIdent] = Show.show(toString)

}

extension (s: String) {
  def pathIdent = PathIdent.from(s)
}

extension (path: NonEmptyList[String]) {
  def pathIdent = PathIdent.from(path)
}
