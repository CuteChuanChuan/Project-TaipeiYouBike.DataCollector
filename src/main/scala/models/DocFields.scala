package models

object DocFields {

  object Dim {
    val TIMESTAMP_UPDATED = "timestamp_updated"
    val STATION_ID        = "station_id"
    val NAME              = "name"
    val NAME_ZH           = "name_zh"
    val ADDRESS           = "address"
    val ADDRESS_ZH        = "address_zh"
    val LATITUDE          = "latitude"
    val LONGITUDE         = "longitude"
    val TOTAL_DOCKS       = "total_docks"
    val DISTRICT_ZH       = "district_zh"
    val IS_ACTIVE         = "is_active"
  }

  object Fct {
    val STATION_ID            = "station_id"
    val TIMESTAMP_FETCHED     = "timestamp_fetched"
    val STATION_DISTRICT      = "station_district"
    val IS_ACTIVE             = "is_active"
    val AVAILABLE_BIKES       = "available_bikes"
    val AVAILABLE_DOCKS       = "available_docks"
    val TIMESTAMP_SRC_UPDATED = "timestamp_src_updated"
  }

  object Metadata {
    val EFFECTIVE_FROM = "effective_from"
    val EFFECTIVE_TO   = "effective_to"
    val IS_CURRENT     = "is_current"
  }
}
