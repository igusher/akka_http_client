package client

import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpEntity, HttpResponse}
import akka.http.scaladsl.model.StatusCodes._
import org.scalamock.scalatest.MockFactory
import org.scalatest.WordSpec

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
 * Created by igusher on 8/19/16.
 */
class RetryingHttpClientSpec extends WordSpec with MockFactory {

  val httpReq = HttpRequest(uri = "uri",
    method = HttpMethods.POST,
    entity = HttpEntity(`application/json`, "body")
  )

  "RetryingHttpClient" should {
    "retry request on 500" in {
      val httpClient = mock[client.HttClient]
      val retryingClient = new RetryingHttpClient(httpClient)
      (httpClient.sendRequest(_)) expects(httpReq) returns(Future.successful(HttpResponse(status = InternalServerError)))
      (httpClient.sendRequest(_)) expects(httpReq) returns(Future.successful(HttpResponse(status = InternalServerError)))
      Await.ready(retryingClient.sendRequest(httpReq), 5 second)
    }
  }

}
