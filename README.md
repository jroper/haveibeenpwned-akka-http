# Akka HTTP support for HaveIBeenPwned password checking

This source code has been provided to show how to use Akka HTTP to check if a password has been found in a data breach published at https://haveibeenpwned.com/passwords.

Please read [this blog post](https://www.troyhunt.com/ive-just-launched-pwned-passwords-version-2/) for a high level description of the password checking service, along with it's k-anonymity support for keeping the passwords you checked private.

The code in this project is intended for copying and pasting into your own project. The file you need is [HaveIBeenPwned.scala](https://github.com/jroper/haveibeenpwned-akka-http/blob/main/src/main/scala/au/id/jazzy/haveibeenpwned/akkahttp/HaveIBeenPwned.scala). It is less than 100 lines of code including comments and copyright notice. I have not published it to a Maven repository, and I have no intention of publishing it to a Maven repository, so don't ask. If you want a maven dependency, go ahead and publish it to a Maven repository yourself. Fork the project if you want.

It requires the Java 17 HexFormat API. If using older Java versions, there are multiple one liners that can be used to hex encode bytes in Scala if you Google for them.

Here's how to use it:

```scala
implicit val system = ActorSystem(Behaviors.empty, "test-system")
import system.executionContext

HaveIBeenPwned.checkPassword("my-password").foreach {
  case Some(count) => println(s"The password has been seen $count times.")
  case None => println("The password has not been seen.")
}
```