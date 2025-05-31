package models

import models.enums.CollectionsUsed.CollectionsUsed

case class MongoConfig(
  uri:         String,
  database:    String,
  collections: Map[CollectionsUsed, String]
)
