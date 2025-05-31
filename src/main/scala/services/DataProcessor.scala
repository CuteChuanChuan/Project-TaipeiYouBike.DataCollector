package services

import models.{ Station, StationDim, StationFct }
import services.converter.ModelConverter

import java.time.ZonedDateTime

class DataProcessor {

  def convertStationsToModels(
    stations:      List[Station],
    retrievedTime: ZonedDateTime): (List[StationFct], List[StationDim]) = {
    val fctRecords = stations.map(station => ModelConverter.stationToFct(station, retrievedTime))
    val dimRecords = stations.map(station => ModelConverter.stationToDim(station, retrievedTime))
    (fctRecords, dimRecords)
  }

}
