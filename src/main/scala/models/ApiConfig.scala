package models

import scala.concurrent.duration.FiniteDuration

case class ApiConfig(url: String, fetchInterval: FiniteDuration)
