package models

import java.time.ZonedDateTime

case class StationFct(
  stationId:           Int,
  timestampFetched:    ZonedDateTime,
  stationDistrict:     String,
  isActive:            Boolean,
  availableBikes:      Int,
  availableDocks:      Int,
  timestampSrcUpdated: ZonedDateTime
)
