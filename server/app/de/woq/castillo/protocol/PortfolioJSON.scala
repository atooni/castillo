package de.woq.castillo.protocol

import de.woq.castillo.model.{Seminar, SeminarDetails}
import play.api.http.{ContentTypes, ContentTypeOf}
import play.api.libs.json._
import Reads._
import play.api.libs.functional.syntax._
import play.api.mvc.Codec

object PortfolioJSON {

  implicit val readsSeminarDetails : Reads[SeminarDetails] = (
    ( __ \ "title").read(minLength[String](1)) and
    ( __ \ "description").read(minLength[String](1)) and
    ( __ \ "trainer").read(minLength[String](1)) and
    ( __ \ "duration").read(min[Int](1))
  )(SeminarDetails)

  implicit val writesSeminarDetails = Json.writes[SeminarDetails]

  implicit val writesSeminar = Json.writes[Seminar]

  implicit val readsSeminar : Reads[Seminar] = (
    ( __ \ "id").read(minLength[String](1)) and
    ( __  \ "details").read[SeminarDetails]
  )(Seminar)

  
  // define the Content-Type for Seminars and SeminarDetails as JSON
  
  implicit def contentTypeOfSeminar(implicit codec: Codec): ContentTypeOf[Seminar] = {
    ContentTypeOf[Seminar](Some(ContentTypes.JSON))
  }

  implicit def contentTypeOfSeminarDetails(implicit codec: Codec): ContentTypeOf[SeminarDetails] = {
    ContentTypeOf[SeminarDetails](Some(ContentTypes.JSON))
  }
}
