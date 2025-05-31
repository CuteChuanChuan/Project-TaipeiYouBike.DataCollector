package models

case class Station(
  id:                   String,
  nameZh:               String,
  districtZh:           String,
  mday:                 String,
  addrZh:               String,
  district:             String,
  name:                 String,
  addr:                 String,
  act:                  String,
  srcUpdateTime:        String,
  updateTime:           String,
  infoTime:             String,
  infoDate:             String,
  total:                Int,
  availableRentBikes:   Int,
  latitude:             Double,
  longitude:            Double,
  availableReturnBikes: Int)
