package app

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import collectors.YouBikeCollector
import org.slf4j.{ Logger, LoggerFactory }

import scala.util.Try

object Main {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {

    val system = ActorSystem(Behaviors.empty, "BikeDataCollectorSystem")

    Try {
      val collector = new YouBikeCollector()(system)
      collector.startScheduledCollecting()

      logger.info("YouBike data collector started. Press CTRL+C to terminate.")

      sys.addShutdownHook {
        logger.info("Terminating actor system...")
        system.terminate()
      }

      Thread.currentThread().join()
    } recover { case e =>
      logger.error(e.getMessage)
    }
  }
}
