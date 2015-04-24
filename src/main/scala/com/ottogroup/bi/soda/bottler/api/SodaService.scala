package com.ottogroup.bi.soda.bottler.api

import com.ottogroup.bi.soda.bottler.SodaRootActor.settings
import akka.actor.ActorSystem
import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directive.pimpApply
import spray.routing.SimpleRoutingApp
import akka.util.Timeout
import scala.concurrent.duration._
import jline.ConsoleReader
import spray.http.HttpHeaders.RawHeader
import jline.History
import java.io.File

object SodaService extends App with SimpleParallelRoutingApp {

  val soda = new SodaSystem()

  implicit val system = ActorSystem("soda-webservice")

  import SodaJsonProtocol._

  startServer(interface = "localhost", port = settings.port) {
    get {
      respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
        parameters("status"?, "filter"?, "dependencies".as[Boolean]?) { (status, filter, dependencies) => {
          path("actions") {
              complete(soda.actions(status, filter))
          } ~
          path("commands") {
              complete(soda.commands(status, filter))
          } ~
          path("views" / Rest ?) { viewUrlPath =>
             complete(soda.views(viewUrlPath, status, filter, dependencies))
          } ~
          path("materialize" / Rest ?) { viewUrlPath =>
            complete(soda.materialize(viewUrlPath, status, filter)) 
          } ~
          path("invalidate" / Rest ?) { viewUrlPath =>
            complete(soda.invalidate(viewUrlPath, status, filter, dependencies))
          } ~
          path("newdata" / Rest ?) { viewUrlPath =>
            complete(soda.newdata(viewUrlPath, status, filter))
          } ~
          path("command" / Rest) { commandId =>
            complete(soda.commandStatus(commandId))
          } ~
          path("graph" / Rest) { viewUrlPath =>
            getFromFile(s"${settings.webResourcesDirectory}/graph.html")
          }
        }
      } }
    }
  }

  Thread.sleep(10000)
  println("SODA")
  println("\n\n============= SODA initialization finished ============== \n\n")
  val ctrl = new SodaControl(soda)
  val reader = new ConsoleReader()
  val history = new History()
  history.setHistoryFile(new File(".soda_history"))
  reader.setHistory(history)
  while (true) {
    try {
      val cmd = reader.readLine("soda> ")
      // we have to intercept --help because otherwise jline seems to call System.exit :(
      if (cmd != null && !cmd.trim().replaceAll(";", "").isEmpty() && !cmd.matches(".*--help.*"))
        ctrl.run(cmd.split("\\s+"))
    } catch {
      case t: Throwable => println(s"ERROR: ${t.getMessage}\n\n"); t.printStackTrace()
    }
  }

}
