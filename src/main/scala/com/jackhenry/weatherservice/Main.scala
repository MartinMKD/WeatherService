package com.jackhenry.weatherservice

import cats.effect.{IO, IOApp}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {
  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  val run: IO[Unit] = {
    Slf4jLogger.create[IO].flatMap {
      _: Logger[IO] => WeatherServiceServer.run[IO]
    }
  }
}