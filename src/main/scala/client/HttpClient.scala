package client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.model.{headers, HttpResponse, HttpRequest}
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
 * Created by igusher on 8/19/16.
 */

trait HttClient {
  def sendRequest(httpRequest: HttpRequest): Future[HttpResponse]
}

class SimpleHttpClient extends HttClient {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val token = ""

  override def sendRequest(httpRequest: HttpRequest): Future[HttpResponse] = {
    val authHeader = Authorization(headers.OAuth2BearerToken(token))
    Http().singleRequest(httpRequest.copy(headers = httpRequest.headers :+ authHeader))
  }
}

class RetryingHttpClient (val httpClient: HttClient) extends HttClient {
  override def sendRequest(httpRequest: HttpRequest): Future[HttpResponse] = {
    httpClient.sendRequest(httpRequest).flatMap(httpResponse => {
      httpResponse.status match {
        // Retry on 500
        case InternalServerError => httpClient.sendRequest(httpRequest)
        case _ => Future.successful(httpResponse)
      }
    })
  }
}

