package client

import scala.concurrent.Future
/**
 * Created by igusher on 8/19/16.
 */
trait UserActionApiExample {
  def sendMessage(sourceUserId: Int, targetUserId: Int, text: String): Future[Unit]
  def viewProfile(sourceUserId: Int, targetUserId: Int): Future[Unit]
}
