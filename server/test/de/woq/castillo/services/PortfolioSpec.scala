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
      val s1 = portfolio.create(details)
      s1 should not be None
      
      val s2 = portfolio.get(s1.get.id)
      s1 should be (s2)
    }
    
    "allow to delete a seminar by it's id" in {
      
      val seminars = portfolio.list()
      
      val s1 = portfolio.create(details)
      s1 should not be None

      val s2 = portfolio.delete(s1.get.id)
      s1 should be (s2)
      
      portfolio.list() should be (seminars)
    }
    
    "allow to update a seminar with it's id" in {

      val s1 = portfolio.create(details)
      s1 should not be None
      
      val s2 = portfolio.update(Seminar(s1.get.id, details.copy(duration = 7)))
      s2 should not be None
      
      val s3 = portfolio.get(s1.get.id)
      s3.get.details.duration should be (7)
    }
    
    "allow to retrieve the list of seminars" in {

      val seminars = portfolio.list()

      val s1 = portfolio.create(details)
      s1 should not be None

      portfolio.list().length should be (seminars.length + 1)
    }
  }
}
