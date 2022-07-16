package pirene.ast

import scala.reflect.TypeTest

type Constant = String | Long | Double | Boolean | Unit
object Constant {

  given [A](using
      quotes: quoted.Quotes,
      ta: quoted.Type[A]
  ): TypeTest[Constant, A] with {
    override def unapply(c: Constant): Option[c.type & A] =
      ???
  }

}
