package controllers 

import scala.concurrent.Future
import org.scalatest._
import org.scalatestplus.play._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import org.scalatest.concurrent._
import org.scalatestplus.play.MixedFixtures

class ValidationSpec 
  extends MixedPlaySpec {

  // TODO: The following test is cancelled:
  // 
  "Validation page" must {
    "Test title" in new Chrome {
      go to (s"http://localhost:$port")
      pageTitle must include("RDFShape")
    }
  }
  
} 