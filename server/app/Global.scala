import de.woq.castillo.model.SeminarDetails
import de.woq.castillo.services.{PortfolioImpl, Portfolio}
import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {
  
  val portfolio : Portfolio = PortfolioImpl

  override def onStart(app: Application): Unit = {
    super.onStart(app)
    
    if (portfolio.list().isEmpty) {
      portfolio.create(SeminarDetails(
        title = "Creating Play applications with a ScalaJS frontend",
        description = "The title says it all",
        trainer = "andreas@wayofquality.de",
        duration = 5
      ))

      portfolio.create(SeminarDetails(
        title = "Using Scala in an OSGi world",
        description = "An odd use case for Scala and Akka",
        trainer = "andreas@wayofquality.de",
        duration = 5
      ))
    }
  }
}
