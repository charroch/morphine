package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee.Enumerator
import play.api.libs.Comet
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def landing = Action {
    Ok(views.html.comet())
  }

  def comet = Action {
    val events = Enumerator("kiki", "foo", "bar")
    import scala.sys.process._
    val adb = "adb logcat".lines_!
    val event2s = Enumerator.enumerate(adb)
    Ok.stream(events &> Comet(callback = "parent.cometMessage"))
  }

}