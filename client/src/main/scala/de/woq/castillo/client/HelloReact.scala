package de.woq.castillo.client

import japgolly.scalajs.react.{ComponentScopeM, ReactComponentB}
import japgolly.scalajs.react.vdom.ReactVDom.all._
import org.scalajs.dom
import org.scalajs.dom.window
import scala.scalajs.js
import scala.scalajs.js.JSApp

object HelloReact extends JSApp{

  case class State(secondsElapsed : Long)

  class Backend {
    var interval : js.UndefOr[Int] = js.undefined
    def tick(scope : ComponentScopeM[_, State, _]) : js.Function =
      () => scope.modState(s => State(s.secondsElapsed + 1))
  }

  val helloCastillo = ReactComponentB[String]("appMenu")
    .initialState(State(0))
    .backend(_ => new Backend)
    .render((param,s,_) => div(
    h2(s"Hello from React, $param !"),
    h3(s"Time elapsed : ${s.secondsElapsed}s")
  ))
    .componentDidMount(scope =>
    scope.backend.interval = window.setInterval(scope.backend.tick(scope), 1000))
    .componentWillUnmount(_.backend.interval foreach window.clearInterval)
    .build

  def main(): Unit = helloCastillo("Andreas") render dom.document.getElementById("content")

}