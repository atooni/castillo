package controllers

import play.api.libs.json._
import play.api.mvc._
import de.woq.castillo.model.{Seminar, SeminarDetails}
import de.woq.castillo.services.{PortfolioImpl, Portfolio}
import de.woq.castillo.protocol.PortfolioJSON._

object PortfolioController extends Controller {

  private[this] val portfolio : Portfolio = PortfolioImpl

  val list = Action { Ok(Json.toJson(portfolio.list())) }

  val create = Action(parse.json) {
    implicit request => request.body.validate[SeminarDetails] match {
      case JsSuccess(createItem, _) =>
        portfolio.create(createItem) match {
          case Some(course) => Ok(Json.toJson(course))
          case None => InternalServerError
        }
      case JsError(errors) => BadRequest
    }
  }

  def update(id: Long) = Action(parse.json) {
    implicit request => request.body.validate[Seminar] match {
      case JsSuccess(seminar, _) => portfolio.update(seminar) match {
        case Some(c) => Ok(Json.toJson(c))
        case None => NotFound
      }
      case JsError(errors) => BadRequest
    }
  }

  def delete(id: Long) = Action {
    portfolio.delete(id) match {
      case Some(course) => Ok(Json.toJson(course))
      case None => NotFound
    }
  }

  def details(id: Long) = Action {

    portfolio.get(id) match {
      case Some(c) => Ok(Json.toJson(c))
      case None => NotFound
    }
  }
}
