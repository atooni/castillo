package de.woq.castillo.app

import de.woq.castillo.model.{Seminar, SeminarDetails}
import de.woq.castillo.protocol.PortfolioJSON._
import org.scalatest.{Matchers, OptionValues, WordSpec}
import org.scalatestplus.play.{OneServerPerSuite, WsScalaTestClient}
import play.api.http.Writeable
import play.api.libs.json._
import play.api.libs.ws.WS
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
  implicit private def jsWriteable[A](
    implicit wa: Writes[A], wjs: Writeable[JsValue]
  ): Writeable[A] = wjs.map(a => Json.toJson(a))

  "The portfolio REST service" should {
    
    "provide the list of available seminars at /portfolio" in {
      val result = Await.result(WS.url(portfolioBase).get(), 1.second)
      result.status should be (OK)

      val seminars = Json.fromJson[Seq[Seminar]](Json.parse(result.body))
      seminars.isSuccess should be (true)

      seminars.get should have size(1)
    }
    
    "allow to retrieve an existing seminar" in {
      val result = Await.result(WS.url(s"$portfolioBase/1").get(), 1.second)
      result.status should be (OK)
      
      val seminar = Json.fromJson[Seminar](Json.parse(result.body))
      seminar.isSuccess should be (true)
      
      seminar.get should be (Seminar(1, SeminarDetails(
        title = "Creating Play applications with a ScalaJS frontend",
        description = "The title says it all",
        trainer = "andreas@wayofquality.de",
        duration = 5
      )))
    } 
    
    "respond with a HTTP NotFound when trying to retrieve a non-existing seminar" in {
      val result = Await.result(WS.url(s"$portfolioBase/${Long.MaxValue}").get(), 1.second)
      result.status should be (NOT_FOUND)
    }
    
    "allow to create a seminar" in {

      val result = Await.result(
        WS.url(portfolioBase)
          .withBody[SeminarDetails](details)
          .withMethod("POST")
          .execute(),
        1.second
      )
      
      result.status should be (OK)

      val seminar = Json.fromJson[Seminar](Json.parse(result.body))
      seminar.isSuccess should be (true)
      seminar.get.details should be (details)
    }
    
    "fail to create a seminar with wrong details" in {

      val result = Await.result(
        WS.url(portfolioBase)
          .withBody[SeminarDetails](details.copy(duration = 0))
          .withMethod("POST")
          .execute(),
        1.second
      )

      result.status should be (BAD_REQUEST)
    }
    
    "allow to delete an object by it's id" in {
      
      val result = Await.result(WS.url(s"$portfolioBase/1").delete(), 1.second)

      result.status should be (OK)
      
      val seminar = Json.fromJson[Seminar](Json.parse(result.body))
      seminar.isSuccess should be (true)
      seminar.get.id should be (1)

      Await.result(WS.url(s"$portfolioBase/1").get(), 1.second).status should be (NOT_FOUND)
    }
    
    "return HTTP 404 when trying to delete a non-existing seminar" in {

      Await.result(WS.url(s"$portfolioBase/${Long.MaxValue}").delete(), 1.second).status should be (NOT_FOUND)
    }
    
    "allow to update an existing seminar" in {
      val result = Await.result(
        WS.url(portfolioBase)
          .withBody[SeminarDetails](details)
          .withMethod("POST")
          .execute(),
        1.second
      )

      result.status should be (OK)

      val seminar = Json.fromJson[Seminar](Json.parse(result.body))
      seminar.isSuccess should be (true)
      seminar.get.details should be (details)
      
      val update = Await.result(
        WS.url(s"$portfolioBase/${seminar.get.id}").put[Seminar](
          seminar.get.copy(details = details.copy(duration = 7))
        ), 1.second
      )
      
      update.status should be (OK)
      val updated = Json.fromJson[Seminar](Json.parse(update.body))
      updated.isSuccess should be (true)
      updated.get.details should be (details.copy(duration = 7))
    }

    "fail if updating the seminar with wrong details" in {
      val result = Await.result(
        WS.url(portfolioBase)
          .withBody[SeminarDetails](details)
          .withMethod("POST")
          .execute(),
        1.second
      )

      result.status should be (OK)

      val seminar = Json.fromJson[Seminar](Json.parse(result.body))
      seminar.isSuccess should be (true)
      seminar.get.details should be (details)

      val update = Await.result(
        WS.url(s"$portfolioBase/${seminar.get.id}").put[Seminar](
          seminar.get.copy(details = details.copy(duration = 0))
        ), 1.second
      ).status should be (BAD_REQUEST)
    }

    "return HTTP 404 if trying to update a non-existing seminar" in {
      Await.result(
        WS.url(s"$portfolioBase/${Long.MaxValue}").put[Seminar](
          Seminar(Long.MaxValue, details = details.copy(duration = 7))
        ), 1.second
      ).status should be (NOT_FOUND)
    }
  }

}
