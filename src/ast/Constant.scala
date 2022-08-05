package pirene.ast

import io.circe.Encoder
import io.circe.Json

type Constant = String | Long | Double | Boolean | Unit
object Constant {

  def asJson(c: Constant) = c match {
    case c: String  => Json.fromString(c)
    case c: Boolean => Json.fromBoolean(c)
    case c: Long    => Json.fromLong(c)
    case c: Double  => Json.fromDoubleOrNull(c)
    case c: Unit    => Json.obj()
  }

  given Encoder[Constant] = asJson(_)

}
