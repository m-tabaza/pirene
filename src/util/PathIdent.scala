package pirene.util

import cats.data.NonEmptyList
import cats.Show

opaque type PathIdent = NonEmptyList[String]
object PathIdent {

  def from(head: String, tail: String*): PathIdent =
    NonEmptyList(head, tail.toList)

  def from(path: NonEmptyList[String]): PathIdent = path

  def toString(ident: PathIdent): String = ident.reduceLeft(_ + "." + _)

  def path(ident: PathIdent): NonEmptyList[String] = ident

  given Show[PathIdent] with {
    def show(ident: PathIdent): String = PathIdent.toString(ident)
  }

}

extension (s: String) {
  def pathIdent = PathIdent.from(s)
}

extension (path: NonEmptyList[String]) {
  def pathIdent = PathIdent.from(path)
}
