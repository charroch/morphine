import scala.io.Source
import unfiltered.response.Html
import unfiltered.netty._

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

    val websocket = websockets.Planify({
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

    Http(5679)
      .handler(websockets.Planify({
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
      case _ => ResponseString(lines)
    })
      .run {
      s =>
        (1 to 4).foreach {
          i =>
            Browser.open("file://%s" format (App.getClass.getResource("client.html").getFile))
        }
    }
  }

  val lines = """
    <! DOCTYPE html >
      <html>
        <head>
          <title>Unfiltered WebSockets</title>
          <style type="text/css">
            *
            {margin: 0; padding: 0;}
            body
            {font - size: 12 px; font - family: helvetica, sans serif;
  padding: 1 em;
  color :# 333;}
  input[type='text']
  {font - size: 12 px;}
  #debug
  {font - size: 12 px;
  color :# ccc;}
  ul
  {list - style: none;}
  .who
  {color :# FF00C3;}</style>
  <script text="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
  </head>
  <body>
    ws://localhost:5679/
    <form id="frm">
      <input type="text" name="message" id="message" placeholder="Hey bro"/>
      <div>
        <input type="button" value="toogle connection" id="tooglr"/>
      </div>
    </form>
    <div id="debug"></div>
    <ul></ul>
    <script type="text/javascript">
      (function(jq)
      {var socket, host = "ws://localhost:5679/";

    var supported = function() {
      return window.WebSocket || window.MozWebSocket;
    }
  , newWebSocket = function(uri) {
    return window.WebSocket ?
      new WebSocket(uri): new MozWebSocket(uri)
  }
  , createSocket = function(uri) {
    if (supported()) {
      window.socket = socket = newWebSocket(uri);
      socket.onmessage = function(e) {
        var msg = e.data.split("|"), who = msg.shift(), what = msg.join("|");
        jq('ul ').first().prepend([ '< li >< span class = "who" > ', who, '</ span > ', what, '</ li > '].join(""));
      }
      socket.onopen = function(e) {
        debug('connection open ')
      }
      socket.onclose = function(e) {
        debug('connection closed ');
      }
    } else {
      alert("your browser does not support web sockets. try chrome.");
    }
  }
  , debug = function(msg) {
    jq("#debug").html(msg);
  }
  , isOpen = function() {
    return socket ?
      socket.readyState == (window.WebSocket ? WebSocket.OPEN: MozWebSocket.OPEN): false;
  }
  , send = function(message) {
    if (!supported()) {
      return;
    }
    if (isOpen()) {
      socket.send(message);
    } else {
      alert("socket is not open");
    }
  }
  , closeSocket = function() {
    if (socket) {
      socket.close();
    }
  }
  , openSocket = function() {
    if (isOpen()) {
      alert('socket already open ');
      return;
    }
    createSocket(host);
  }
  , toggleConnection = function() {
    if (isOpen()) {
      closeSocket();
    } else {
      openSocket();
    }
  };

  createSocket(host);

  jq("#message").bind('keydown ', function() {
    jq("#submit").removeAttr("disabled");
    jq(this).unbind('keydown ');
  });
  jq("#frm").submit(function(e) {
    e.preventDefault();
    send(this.message.value);
    this.message.value = ' ';
    return false;
  });
  jq("#tooglr").click(function(e) {
    e.preventDefault();
    toggleConnection();
    return false;
  });}
  )(jQuery);
  </script>
  </body>
  </html>     """
}
