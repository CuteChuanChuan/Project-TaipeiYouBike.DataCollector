package services.converter

import spray.json.{ DefaultJsonProtocol, JsValue, RootJsonFormat }
import models.Station

object StationProtocol extends DefaultJsonProtocol {

  implicit val StationJsonFormat: RootJsonFormat[Station] = new RootJsonFormat[Station] {
    def read(value: JsValue): Station = {
      val fields = value.asJsObject.fields
      Station(
        id = fields("sno").convertTo[String],
        nameZh = fields("sna").convertTo[String],
        districtZh = fields("sarea").convertTo[String],
        mday = fields("mday").convertTo[String],
        addrZh = fields("ar").convertTo[String],
        district = fields("sareaen").convertTo[String],
        name = fields("snaen").convertTo[String],
        addr = fields("aren").convertTo[String],
        act = fields("act").convertTo[String],
        srcUpdateTime = fields("srcUpdateTime").convertTo[String],
        updateTime = fields("updateTime").convertTo[String],
        infoTime = fields("infoTime").convertTo[String],
        infoDate = fields("infoDate").convertTo[String],
        total = fields("total").convertTo[Int],
        availableRentBikes = fields("available_rent_bikes").convertTo[Int],
        latitude = fields("latitude").convertTo[Double],
        longitude = fields("longitude").convertTo[Double],
        availableReturnBikes = fields("available_return_bikes").convertTo[Int]
      )
    }

    override def write(obj: Station): JsValue = ???
  }

  implicit val stationListFormat: RootJsonFormat[List[Station]] = listFormat[Station]

}
