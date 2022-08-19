package pirene.client

import org.scalajs.dom.document
import slinky.core.*
import slinky.web.ReactDOM
import slinky.web.html.*

@main def main = ReactDOM.render(
  h1("Welcome to Pirene!"),
  document.getElementById("root")
)
