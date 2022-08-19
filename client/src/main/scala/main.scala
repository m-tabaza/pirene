package pirene.client

import org.scalajs.dom.document
import slinky.core.*
import slinky.core.facade.Hooks
import slinky.web.ReactDOM
import slinky.web.html.*

@main def main = ReactDOM.render(
  App.component("Welcome to Pirene"),
  document.getElementById("root")
)

object App {
  type Props = String

  val component = FunctionalComponent[Props] { props =>
    val (count, setCount) = Hooks.useState(1)

    div(
      h1(props + "!" * count),
      button(onClick := { () => setCount(count + 1) }, "Click me")
    )
  }
}
