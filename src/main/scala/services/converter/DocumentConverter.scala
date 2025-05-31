package services.converter

import models.{ DocFields, ScdMetadata, StationDim, StationFct }
import org.bson.Document
import org.mongodb.scala.bson.{ BsonBoolean, BsonDouble, BsonInt32, BsonString }

import java.time.format.DateTimeFormatter
import java.time.{ ZoneId, ZonedDateTime }

object DocumentConverter {

  private lazy val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

  def fctToDocument(fct: StationFct): Document = {
    val fetchedTimeStr    = formatZonedDateTime(fct.timestampFetched)
    val srcUpdatedTimeStr = formatZonedDateTime(fct.timestampSrcUpdated)
    new Document()
      .append(DocFields.Fct.STATION_ID, BsonInt32(fct.stationId))
      .append(DocFields.Fct.TIMESTAMP_FETCHED, fetchedTimeStr)
      .append(DocFields.Fct.STATION_DISTRICT, BsonString(fct.stationDistrict))
      .append(DocFields.Fct.IS_ACTIVE, BsonBoolean(fct.isActive))
      .append(DocFields.Fct.AVAILABLE_BIKES, BsonInt32(fct.availableBikes))
      .append(DocFields.Fct.AVAILABLE_DOCKS, BsonInt32(fct.availableDocks))
      .append(DocFields.Fct.TIMESTAMP_SRC_UPDATED, srcUpdatedTimeStr)
  }

  def dimToDocument(dim: StationDim, metadata: ScdMetadata): Document = {
    val updatedTimeStr   = formatZonedDateTime(dim.timestampUpdated)
    val effectiveFromStr = formatZonedDateTime(metadata.effectiveFrom)
    val effectiveToStr   = metadata.effectiveTo.map(time => formatZonedDateTime(time)).orNull

    new Document()
      .append(DocFields.Dim.TIMESTAMP_UPDATED, updatedTimeStr)
      .append(DocFields.Dim.STATION_ID, BsonInt32(dim.stationId))
      .append(DocFields.Dim.NAME, BsonString(dim.name))
      .append(DocFields.Dim.NAME_ZH, BsonString(dim.nameZh))
      .append(DocFields.Dim.ADDRESS, BsonString(dim.address))
      .append(DocFields.Dim.ADDRESS_ZH, BsonString(dim.addressZh))
      .append(DocFields.Dim.LATITUDE, BsonDouble(dim.latitude))
      .append(DocFields.Dim.LONGITUDE, BsonDouble(dim.longitude))
      .append(DocFields.Dim.TOTAL_DOCKS, BsonInt32(dim.totalDocks))
      .append(DocFields.Dim.DISTRICT_ZH, BsonString(dim.districtZh))
      .append(DocFields.Dim.IS_ACTIVE, BsonBoolean(dim.isActive))
      .append(DocFields.Metadata.EFFECTIVE_FROM, effectiveFromStr)
      .append(DocFields.Metadata.EFFECTIVE_TO, effectiveToStr)
      .append(DocFields.Metadata.IS_CURRENT, BsonBoolean(metadata.isCurrent))
  }

  private def formatZonedDateTime(time: ZonedDateTime): String =
    time.withZoneSameInstant(ZoneId.of("UTC")).format(timeFormatter)
}
