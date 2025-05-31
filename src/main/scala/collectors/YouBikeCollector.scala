package collectors

import akka.actor.typed.ActorSystem
import helper.ConfigHelper
import models.Station
import org.slf4j.{ Logger, LoggerFactory }
import repositories.StationRepository
import services.{ ApiFetcher, DataProcessor, RawJsonParser, ScdProcessor }

import java.time.ZonedDateTime
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

class YouBikeCollector()(implicit system: ActorSystem[_]) {

  private implicit val ec:   ExecutionContext  = system.executionContext
  private val logger:        Logger            = LoggerFactory.getLogger(this.getClass)
  private val stationRepo:   StationRepository = new StationRepository(new ScdProcessor())
  private val dataProcessor: DataProcessor     = new DataProcessor(stationRepo)

  def startScheduledCollecting(): Unit = {
    val fetchInterval = ConfigHelper.fetchApiConfig.fetchInterval

    system.scheduler.scheduleAtFixedRate(0.seconds, fetchInterval) { () =>
      logger.info(s"Executing scheduled collection at: ${ZonedDateTime.now()}")

      collectThenProcess().onComplete {
        case Success(_)         =>
          logger.info("Scheduled collection completed successfully")
        case Failure(exception) =>
          logger.error(s"Scheduled collection failed: ${exception.getMessage}")
      }
    }
  }

  private def collectThenProcess()(implicit system: ActorSystem[_]): Future[Unit] =
    for {
      stations <- collectData()
      _ = processData(stations)
    } yield ()

  private def collectData()(implicit system: ActorSystem[_]): Future[List[Station]] =
    for {
      rawData <- new ApiFetcher()(system).fetchApi()
      stations = RawJsonParser.parseJsonData(rawData)
    } yield stations

  private def processData(stations: List[Station]): Future[Unit] =
    dataProcessor.processStations(stations)

}
