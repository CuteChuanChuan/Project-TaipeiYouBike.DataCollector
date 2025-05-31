package helper

import com.typesafe.config.{ Config, ConfigFactory }
import models.{ ApiConfig, MongoConfig }
import org.slf4j.{ Logger, LoggerFactory }

import scala.concurrent.duration.DurationLong

object ConfigHelper {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)
  private val config: Config = ConfigFactory.load()

  def fetchApiConfig: ApiConfig = {
    val apiConfig = config.getConfig("api")
    ApiConfig(
      url = apiConfig.getString("url"),
      fetchInterval = apiConfig.getDuration("fetch-interval").toNanos.nanos
    )
  }

  def fetchMongoConfig: MongoConfig = {
    val mongoConfig = config.getConfig("mongodb")
    MongoConfig(
      uri = determineMongoUri(mongoConfig),
      database = mongoConfig.getString("database"),
      dimCollection = mongoConfig.getString("collections.dim"),
      fctCollection = mongoConfig.getString("collections.fct")
    )
  }

  private def determineMongoUri(mongoConfig: Config): String = {
    val defaultUri: String = mongoConfig.getString("uri")
    val uri = sys.env.get("MONGODB_URI").orElse(sys.props.get("mongodb.uri")).getOrElse(defaultUri)
    logger.info(s"Using MongoDB URI: $uri")
    uri
  }

  def main(args: Array[String]): Unit = {
    logger.info(s"API Config: $fetchApiConfig")
    logger.info(s"Mongo Config: $fetchMongoConfig")
  }

}
