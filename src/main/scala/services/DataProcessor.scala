package services

import models.{ Station, StationDim, StationFct }
import repositories.StationRepository
import services.converter.ModelConverter

import java.time.ZonedDateTime
import scala.concurrent.{ ExecutionContext, Future }

class DataProcessor(stationRepository: StationRepository)(implicit ec: ExecutionContext) {

  def processStations(stations: List[Station])(implicit ec: ExecutionContext): Future[Unit] = {
    val retrievedTime            = ZonedDateTime.now()
    val (fctRecords, dimRecords) = convertStations(stations, retrievedTime)

    for {
      _ <- stationRepository.insertStationFacts(fctRecords)
      _ <- stationRepository.insertStationDims(dimRecords, retrievedTime)
    } yield ()
  }

  private def convertStations(
    stations:      List[Station],
    retrievedTime: ZonedDateTime): (List[StationFct], List[StationDim]) = {
    val fctRecords = stations.map(station => ModelConverter.stationToFct(station, retrievedTime))
    val dimRecords = stations.map(station => ModelConverter.stationToDim(station, retrievedTime))
    (fctRecords, dimRecords)
  }

}
