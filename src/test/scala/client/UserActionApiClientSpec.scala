package client

import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpResponse, HttpEntity, HttpMethods, HttpRequest}
import org.scalamock.scalatest.MockFactory
import org.scalatest.WordSpec

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
 * Created by igusher on 8/19/16.
 */
class UserActionApiClientSpec extends WordSpec with MockFactory {

  val httpReq = HttpRequest(uri = "uri",
    method = HttpMethods.POST,
    entity = HttpEntity(`application/json`, "body")
  )
  val httpClient = mock[HttClient]
  val apiClient = new UserActionApiClient(httpClient)

  "UserActionApiClient" should {
    "fail with RequestFailedException on 400" in {

      (httpClient.sendRequest(_)) expects(httpReq) returns(Future.successful(HttpResponse(status = BadRequest)))
      intercept[RequestFailedException] {
        Await.result(apiClient.sendRequest(httpReq), 5 second)
      }
    }
    "fail with RequestFailedException on 401" in {
      (httpClient.sendRequest(_)) expects(httpReq) returns(Future.successful(HttpResponse(status = Unauthorized)))

      intercept[RequestFailedException] {
        Await.result(apiClient.sendRequest(httpReq), 5 second)
      }
    }

    "pass through on 200" in {
      (httpClient.sendRequest(_)) expects(httpReq) returns(Future.successful(HttpResponse(status = OK)))

       assert(() === Await.result(apiClient.sendRequest(httpReq), 5 second))
    }
  }
}
