object App {

  import unfiltered.netty.websockets._
  import unfiltered.util._
  import scala.collection.concurrent.Map
  import unfiltered.response.ResponseString

  def main(args: Array[String]) {
    import scala.collection.JavaConversions._

    val sockets: Map[Int, WebSocket] = new java.util.concurrent.ConcurrentHashMap[Int, WebSocket]

    def notify(msg: String) = sockets.values.foreach {
      s =>
        if (s.channel.isConnected) s.send(msg)
    }

    def notify2(msg: String) = sockets.values.foreach {
      s =>
        if (s.channel.isConnected) s.send(msg)
    }

    import scala.actors._

    object SillyActor extends Actor {
      def act() {
        import scala.sys.process._
        "adb logcat".lines_!.foreach(line => {
          notify2(line)
        })
      }
    }

    val a = SillyActor.start



    unfiltered.netty.Http(5679).handler(unfiltered.netty.websockets.Planify({
      case _ => {
        case Open(s) =>
          notify("%s|joined" format s.channel.getId)
          sockets += (s.channel.getId.intValue -> s)
          s.send("sys|hola!")
        case Message(s, Text(msg)) =>
          notify("%s|%s" format(s.channel.getId, msg))
        case Close(s) =>
          sockets -= s.channel.getId.intValue
          notify("%s|left" format s.channel.getId)
        case Error(s, e) =>
          e.printStackTrace
      }
    })
      .onPass(_.sendUpstream(_)))
      .handler(unfiltered.netty.cycle.Planify {
      case _ => ResponseString(App.getClass.getResource("client.html").openStream())
    })
      .run {
      s =>
        (1 to 4).foreach {
          i =>
            Browser.open("file://%s" format (App.getClass.getResource("client.html").getFile))
        }
    }


  }
}
