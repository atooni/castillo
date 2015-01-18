package de.woq.castillo.services

import de.woq.castillo.model.{Seminar, SeminarDetails}
import org.scalatest.{Matchers, WordSpec}

class PortfolioSpec extends WordSpec 
  with Matchers {

  val portfolio : Portfolio = PortfolioImpl
  
  val details = SeminarDetails(
    title        = "Creating Play applications with a ScalaJS frontend",
    description  = "The title says it all",
    trainer      = "andreas@wayofquality.de",
    duration     = 5
  )

  "A portfolio service" should {
    
    "allow to look up a seminar by it's id" in {
      portfolio.create(details) match {
        case None => fail("Failed to create Seminar")
        case Some(s) => Some(s) should be (portfolio.get(s.id))
      }
    }
    
    "allow to delete a seminar by it's id" in {
      
      val seminars = portfolio.list()
      
      portfolio.create(details) match {
        case None => fail("Failed to create Seminar")
        case Some(s) => 
          Some(s) should be (portfolio.delete(s.id))
          portfolio.list() should be (seminars)
      }
    }
    
    "allow to update a seminar with it's id" in {

      portfolio.create(details) match {
        case None => fail("Failed to create Seminar")
        case Some(s) =>
          portfolio.update(Seminar(s.id, details.copy(duration = 7))) should not be None
          Some(s.copy(details = details.copy(duration = 7))) should be (portfolio.get(s.id))
      }
    }
    
    "allow to retrieve the list of seminars" in {

      val seminars = portfolio.list()

      val s1 = portfolio.create(details)
      s1 should not be None

      portfolio.list().length should be (seminars.length + 1)
    }
  }
}
