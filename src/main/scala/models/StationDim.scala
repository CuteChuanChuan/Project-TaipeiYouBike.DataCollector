package models

import java.time.ZonedDateTime

case class StationDim(
  timestampUpdated: ZonedDateTime,
  stationId:        Int,
  name:             String,
  nameZh:           String,
  address:          String,
  addressZh:        String,
  latitude:         Double,
  longitude:        Double,
  totalDocks:       Int,
  districtZh:       String,
  isActive:         Boolean
)
