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
import akka.actor.typed.scaladsl.Behaviors
import org.scalatest.{BeforeAndAfterAll, OptionValues}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec

class HaveIBeenPwnedSpec extends AnyWordSpec
  with Matchers
  with BeforeAndAfterAll
  with ScalaFutures
  with OptionValues {

  private implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty, "HaveIBeenPwnedSpec")
  private implicit val patience: PatienceConfig = PatienceConfig(timeout = Span(10, Seconds), interval = Span(100, Millis))

  "The HaveIBeenPwned password check" should {
    "return many uses of password" in {
      // At time of writing this test, "password" was used over 9 million times
      HaveIBeenPwned.checkPassword("password").futureValue.value should be > 1000000
    }

    "return a small number of uses of correct horse battery staple" in {
      // At time of writing this test, "correct horse battery staple" returned 5 uses
      val count = HaveIBeenPwned.checkPassword("correct horse battery staple").futureValue.value
      count should be > 1
      count should be < 100
    }

    "return no uses of fda2cee2-e938-4457-aa98-fd6e3f32bf40" in {
      // At time of writing this test, this UUID returned no uses
      HaveIBeenPwned.checkPassword("fda2cee2-e938-4457-aa98-fd6e3f32bf40").futureValue shouldBe None
    }
  }

  override protected def afterAll(): Unit = {
    system.terminate()
  }
}
