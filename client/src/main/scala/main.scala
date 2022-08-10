package pirene.client

import japgolly.scalajs.react.vdom.html_<^.*
import org.scalajs.dom.document

val App = <.h1(<.i("Hello"))

@main def main = App.renderIntoDOM(document.getElementById("root"))
