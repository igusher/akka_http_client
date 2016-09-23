package client

import java.util.logging.Level

import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpMethods, HttpEntity, HttpResponse, HttpRequest}
import com.typesafe.config.ConfigFactory
import dto.{ErrorsDto, ViewProfileDto, SendMessageDto}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try
import spray.json._

import spray.json.DefaultJsonProtocol
import DefaultJsonProtocol._

/**
 * Created by igusher on 8/19/16.
 */
class UserActionApiClient (val httpClient: client.HttClient) extends UserActionApiExample {
  implicit val sendMsgFormat = jsonFormat3(SendMessageDto)
  implicit val viewProfileFormat = jsonFormat2(ViewProfileDto)
  implicit val errorsFormat = jsonFormat1(ErrorsDto)

  val logger = java.util.logging.Logger.getLogger(this.getClass.getName)
  val url = Try(ConfigFactory.load().getString("api.url")).getOrElse("https://...")

  override def sendMessage(sourceUserId: Int, targetUserId: Int, text: String): Future[Unit] = {
    val sendMessageDto = SendMessageDto(sourceUserId, targetUserId, text)
    sendMessageApiCall(sendMessageDto)
  }

  override def viewProfile(sourceUserId: Int, targetUserId: Int): Future[Unit] = {
    val viewProfileDto = ViewProfileDto(sourceUserId, targetUserId)
    viewProfileApiCall(viewProfileDto)
  }


  private def sendMessageApiCall(sendMessageDto: SendMessageDto): Future[Unit] = {
    val entity = HttpEntity(`application/json`, sendMessageDto.toJson.toString())
    sendRequest(HttpRequest(uri = url,
      method = HttpMethods.POST,
      entity = entity
    ))
  }

  private def viewProfileApiCall(viewProfileDto: ViewProfileDto): Future[Unit] = {
    val entity = HttpEntity(`application/json`, viewProfileDto.toJson.toString())
    sendRequest(HttpRequest(uri = url,
      method = HttpMethods.POST,
      entity = entity
    ))
  }

  private[client] def sendRequest(httpRequest: HttpRequest): Future[Unit] = {
    val request = httpClient.sendRequest(httpRequest).map(httpResponse => {
      httpResponse.status match {
        case OK => ()
        case BadRequest | Unauthorized | UnprocessableEntity => {
          throw new RequestFailedException(s"Request failed: ${httpResponse.status}. ")
        }
        case InternalServerError => {
          throw new RequestFailedException(s"Request retry failed.")
        }
        case _ => throw new RequestFailedException(s"Request failed with unexpected response. ${httpResponse.status}")
      }
    })
    request.onFailure ({case t: Throwable => logger.log(Level.SEVERE, "Request failed", t)})
    request
  }

}
