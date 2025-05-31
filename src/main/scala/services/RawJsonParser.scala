package services

import models.Station

object RawJsonParser {

  def parseJsonData(jsonData: String): List[Station] = {
    import services.converter.StationProtocol._
    import spray.json._
    jsonData.parseJson.convertTo[List[Station]]
  }

}
