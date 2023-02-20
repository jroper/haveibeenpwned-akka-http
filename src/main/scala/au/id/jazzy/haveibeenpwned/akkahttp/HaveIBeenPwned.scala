/**
 * MIT No Attribution
 *
 * Copyright 2023 James Roper
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package au.id.jazzy.haveibeenpwned.akkahttp

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest

import java.security.MessageDigest
import java.util.HexFormat
import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Uses the have i been pwned password API to check if a password has been seen in a data breach before, using
 * k-anonymity privacy.
 *
 * See https://haveibeenpwned.com/API/v3#PwnedPasswords for details of the API itself, and
 * https://www.troyhunt.com/ive-just-launched-pwned-passwords-version-2/ for a more high level description of
 * the API.
 *
 * @author James Roper
 */
object HaveIBeenPwned {

  /**
   * Check whether the given password has been seen in any data breaches.
   *
   * @param password The password to check.
   * @return The number of times the password has been used in known breaches, or None if the password is not known.
   */
  def checkPassword(password: String)(implicit system: ActorSystem[_]): Future[Option[Int]] = {
    val hashBytes = MessageDigest.getInstance("SHA-1")
      .digest(password.getBytes("UTF-8"))
    val hash = HexFormat.of().withUpperCase().formatHex(hashBytes)
    val kPrefix = hash.take(5)
    val kSuffix = hash.drop(5)
    val regex = s"(?m)^$kSuffix:(\\d+)$$".r

    import system.executionContext

    Http().singleRequest(HttpRequest(uri = s"https://api.pwnedpasswords.com/range/$kPrefix"))
      .flatMap { response =>
        if (response.status.isSuccess()) {
          response.entity.toStrict(10.seconds)
        } else {
          Future.failed(new RuntimeException(s"haveibeenpwned password API returned non success response: ${response.status}"))
        }
      }.map { strict =>
      regex.findFirstMatchIn(strict.data.utf8String)
        .map(_.group(1).toInt)
    }
  }
}
