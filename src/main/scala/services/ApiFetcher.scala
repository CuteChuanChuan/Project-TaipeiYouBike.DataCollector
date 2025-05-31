package services

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import helper.ConfigHelper

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.DurationInt

class ApiFetcher(implicit system: ActorSystem[_]) {

  private implicit val ec: ExecutionContext = system.executionContext

  def fetchApi()(implicit system: ActorSystem[_]): Future[String] =
    for {
      response <- Http()(system).singleRequest(HttpRequest(uri = ConfigHelper.fetchApiConfig.url))
      strictEntity <- response.entity.toStrict(5.seconds)
      data = strictEntity.getData().utf8String
    } yield data

}
