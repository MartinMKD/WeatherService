package com.jackhenry.weatherservice

import cats.effect.kernel.Sync
import cats.effect.{Async, Resource}
import com.comcast.ip4s._
import fs2.io.net.Network
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object WeatherServiceServer {
  final case class AppConfig(appId: String)

  def run[F[_]: Async: Network : org.typelevel.log4cats.Logger]: F[Nothing] = {
    for {
      client <- EmberClientBuilder.default[F].build
      config <- Resource.eval(Sync[F].delay { ConfigSource.default.loadOrThrow[AppConfig] })
      weatherAlg = WeatherService.impl[F](client, config)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract segments not checked
      // in the underlying routes.
      httpApp = WeatherServiceRoutes.weatherServiceRoutes[F](weatherAlg).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(httpApp)
      _ <-
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever
}