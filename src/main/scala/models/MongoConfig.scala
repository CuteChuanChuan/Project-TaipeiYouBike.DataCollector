package models

case class MongoConfig(
  uri:           String,
  database:      String,
  dimCollection: String,
  fctCollection: String
)
