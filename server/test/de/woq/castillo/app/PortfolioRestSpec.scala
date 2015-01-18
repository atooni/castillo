package de.woq.castillo.app

import de.woq.castillo.model.{Seminar, SeminarDetails}
import de.woq.castillo.protocol.PortfolioJSON._
import org.scalatest.{Matchers, OptionValues, WordSpec}
import org.scalatestplus.play.{OneServerPerSuite, WsScalaTestClient}
import play.api.http.Writeable
import play.api.libs.json._
import play.api.libs.ws.{WSRequestHolder, WS}
import play.mvc.Http.Status._

import scala.concurrent.Await
import scala.concurrent.duration._

class PortfolioRestSpec extends WordSpec
  with Matchers
  with OptionValues
  with WsScalaTestClient
  with OneServerPerSuite {
  
  private[this] lazy val portfolioBase = s"http://localhost:$port/portfolio"

  private[this] val details = SeminarDetails(
    title = "Yoga and IT",
    description = "Transcend to new programming skills",
    trainer = "andreas@wayofquality.de",
    duration = 5
  )

  // Tranform a Writes[A] into a Writable[A]
  implicit private[this] def jsWriteable[A](
    implicit wa: Writes[A], wjs: Writeable[JsValue]
  ): Writeable[A] = wjs.map(a => Json.toJson(a))
  
  private[this] def executeWsRequest(request : WSRequestHolder) =
    Await.result(request.execute(), 2.second)
  
  private[this] def createSeminar(details : SeminarDetails) = {
    
    val result = executeWsRequest(
      WS.url(portfolioBase)
        .withBody[SeminarDetails](details)
        .withMethod("POST")
    )

    result.status should be (OK)
    validateJSON[Seminar](result.body)
  }
  
  private[this] def validateJSON[T](json : String)(implicit reads: Reads[T]) : Option[T] = {
    val result = Json.fromJson[T](Json.parse(json)).asOpt
    
    result match { 
      case None => fail("Failed to validate JSON response")
      case _ =>
    }
    
    result
  }

  "The portfolio REST service" should {
    
    "provide the list of available seminars at /portfolio" in {
      val result = executeWsRequest(WS.url(portfolioBase).withMethod("GET"))
      result.status should be (OK)
      validateJSON[Seq[Seminar]](result.body)
    }
    
    "allow to retrieve an existing seminar" in {
      val result = executeWsRequest(WS.url(s"$portfolioBase/1").withMethod("GET"))
      result.status should be (OK)
      
      val seminar = validateJSON[Seminar](result.body)
      seminar should not be None
      
      seminar.foreach(_ should be (Seminar(1, SeminarDetails(
        title = "Creating Play applications with a ScalaJS frontend",
        description = "The title says it all",
        trainer = "andreas@wayofquality.de",
        duration = 5
      ))))
    } 
    
    "respond with a HTTP NotFound when trying to retrieve a non-existing seminar" in {
      executeWsRequest(WS.url(s"$portfolioBase/${Long.MaxValue}").withMethod("GET")).status should be (NOT_FOUND)
    }
    
    "allow to create a seminar" in {
      createSeminar(details) should not be None
    }
    
    "fail to create a seminar with wrong details" in {

      executeWsRequest(
        WS.url(portfolioBase)
          .withBody[SeminarDetails](details.copy(duration = 0))
          .withMethod("POST")
      ).status should be (BAD_REQUEST)
    }
    
    "allow to delete an object by it's id" in {
      
      val result = executeWsRequest(WS.url(s"$portfolioBase/1").withMethod("DELETE"))

      result.status should be (OK)
      
      val seminar = validateJSON[Seminar](result.body)
      seminar should not be None
      seminar.foreach(_.id should be (1))

      executeWsRequest(WS.url(s"$portfolioBase/1").withMethod("GET")).status should be (NOT_FOUND)
    }
    
    "return HTTP 404 when trying to delete a non-existing seminar" in {
      executeWsRequest(WS.url(s"$portfolioBase/${Long.MaxValue}").withMethod("DELETE")).status should be (NOT_FOUND)
    }
    
    "allow to update an existing seminar" in {
      createSeminar(details) match {
        case None => fail("Seminar for update not created")
        case Some(s) =>
          val update = executeWsRequest(
            WS.url(s"$portfolioBase/${s.id}")
              .withBody[Seminar](s.copy(details = details.copy(duration = 7)))
              .withMethod("PUT")
          )

          update.status should be (OK)
          val updated = validateJSON[Seminar](update.body)
          updated should not be None
          updated.foreach(_.details should be (details.copy(duration = 7)))
      }
    }

    "fail if updating the seminar with wrong details" in {
      createSeminar(details) match {
        case None => fail("Seminar for update not created")
        case Some(s) =>
          executeWsRequest(
            WS.url(s"$portfolioBase/${s.id}")
              .withBody[Seminar](s.copy(details = details.copy(duration = 0)))
              .withMethod("PUT")
          ).status should be (BAD_REQUEST)
      }
    }

    "return HTTP 404 if trying to update a non-existing seminar" in {
      executeWsRequest(
        WS.url(s"$portfolioBase/${Long.MaxValue}")
          .withBody[Seminar](Seminar(Long.MaxValue, details))
          .withMethod("PUT")
      ).status should be (NOT_FOUND)
    }
  }
}
