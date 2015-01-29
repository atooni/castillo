package de.woq.castillo.client

import de.woq.castillo.model.Seminar
import japgolly.scalajs.react.vdom.ReactVDom.all._
import japgolly.scalajs.react.{React, ReactComponentB}
import org.scalajs.dom
import org.scalajs.dom.extensions.Ajax

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.JSApp
import upickle._

object HelloReact extends JSApp{

  case class State(portfolio : Seq[Seminar])

  class Backend

  val helloCastillo = ReactComponentB[Unit]("HelloCastillo")
    .initialState(State(List()))
    .backend(_ => new Backend)
    .render((_,s,_) => {
      def createSeminar(seminar: Seminar) = div(
        h3(s"${seminar.details.title}"),
        p(s"Trainer ${seminar.details.trainer} -- ${seminar.details.duration} days")
      )
    
      div(
        h2(s"Our course portfolio"),
        div(s.portfolio map createSeminar)
      )
    })
    .componentDidMount(scope => {
      val url = "/portfolio"
      Ajax.get(url).foreach { xhr =>
        println(xhr.responseText)
        val seminars = upickle.read[Seq[Seminar]](xhr.responseText)
        scope.setState(State(seminars))
      }
    })
    .buildU

  def main(): Unit = React.render(helloCastillo(), dom.document.getElementById("content"))

}