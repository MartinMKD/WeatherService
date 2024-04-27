package com.jackhenry.weatherservice

import cats.effect.{IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[Nothing] = {
    WeatherServiceServer.run[IO]
  }
}