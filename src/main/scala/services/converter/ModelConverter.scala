package services.converter

import models.{DocFields, ScdMetadata, Station, StationDim, StationFct}
import org.bson.Document

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId, ZonedDateTime}

/** Object responsible for converting data from API or MongoDB documents to models
  */
object ModelConverter {

  private lazy val timeFormatter:      DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
  private lazy val timeFormatterInUTC: DateTimeFormatter = timeFormatter.withZone(ZoneId.of("UTC"))

  def stationToDim(station: Station, retrievedTime: ZonedDateTime): StationDim =
    StationDim(
      timestampUpdated = retrievedTime,
      stationId = station.id.toInt,
      name = station.name,
      nameZh = station.nameZh,
      address = station.addr,
      addressZh = station.addrZh,
      latitude = station.latitude,
      longitude = station.longitude,
      totalDocks = station.total,
      districtZh = station.districtZh,
      isActive = station.act == "1"
    )

  def stationToFct(station: Station, retrievedTime: ZonedDateTime): StationFct = {
    val localDateTime =
      LocalDateTime.parse(station.srcUpdateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    val timestampSourceUpdated =
      localDateTime.atZone(ZoneId.of("Asia/Taipei")).withZoneSameInstant(ZoneId.of("UTC"))

    StationFct(
      timestampFetched = retrievedTime,
      stationId = station.id.toInt,
      stationDistrict = station.district,
      isActive = station.act == "1",
      availableBikes = station.availableRentBikes,
      availableDocks = station.availableReturnBikes,
      timestampSrcUpdated = timestampSourceUpdated
    )
  }

  def documentToDim(doc: Document): StationDim = {
    val timestampUpdatedStr = doc.getString(DocFields.Dim.TIMESTAMP_UPDATED)
    val timestampUpdated    = ZonedDateTime.parse(timestampUpdatedStr, timeFormatterInUTC)

    StationDim(
      timestampUpdated = timestampUpdated,
      stationId = doc.getInteger(DocFields.Dim.STATION_ID),
      name = doc.getString(DocFields.Dim.NAME),
      nameZh = doc.getString(DocFields.Dim.NAME_ZH),
      address = doc.getString(DocFields.Dim.ADDRESS),
      addressZh = doc.getString(DocFields.Dim.ADDRESS_ZH),
      latitude = doc.getDouble(DocFields.Dim.LATITUDE),
      longitude = doc.getDouble(DocFields.Dim.LONGITUDE),
      totalDocks = doc.getInteger(DocFields.Dim.TOTAL_DOCKS),
      districtZh = doc.getString(DocFields.Dim.DISTRICT_ZH),
      isActive = doc.getBoolean(DocFields.Dim.IS_ACTIVE)
    )
  }

  def documentToSCDMetadata(doc: Document): ScdMetadata = {
    val effectiveFromStr = doc.getString(DocFields.Metadata.EFFECTIVE_FROM)
    val effectiveFrom    = ZonedDateTime.parse(effectiveFromStr, timeFormatterInUTC)

    val effectiveTo = Option(doc.getString(DocFields.Metadata.EFFECTIVE_TO))
      .map(str => ZonedDateTime.parse(str, timeFormatterInUTC))

    ScdMetadata(effectiveFrom, effectiveTo, doc.getBoolean(DocFields.Metadata.IS_CURRENT))
  }

}
