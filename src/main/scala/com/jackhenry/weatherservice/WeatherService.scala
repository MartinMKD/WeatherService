package com.jackhenry.weatherservice

import cats.effect._
import cats.implicits._
import com.jackhenry.weatherservice.WeatherServiceServer.AppConfig
import com.typesafe.scalalogging.LazyLogging
import org.http4s.Method._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits._
import org.typelevel.log4cats.Logger

trait WeatherService[F[_]] {
  def getWeather(lat: Double, lon: Double): F[OpenWeatherModel.OneCallRootElement]
}

object WeatherService extends LazyLogging {
  private final case class WeatherException(e: Throwable) extends RuntimeException

  private def path(lat: Double, lon: Double, config: AppConfig) = {
    uri"https://api.openweathermap.org/data/3.0/onecall"
      .withQueryParams(
        Map(
          "lat"   -> lat.toString,
          "lon"   -> lon.toString,
          "exclude" -> "hourly,daily,minutely",
          "units" -> "imperial",
          "appid" -> config.appId
        )
      )
  }

  private def logError[F[_]: Logger]: PartialFunction[Throwable, F[Unit]] = {
    case e: Exception      => Logger[F].error(e)("getWeather() error")
  }

  def impl[F[_]: Concurrent : Logger](client: Client[F], config: AppConfig): WeatherService[F] = new WeatherService[F] {

    val dsl: Http4sClientDsl[F] = new Http4sClientDsl[F]{}
    import dsl._

    def getWeather(lat: Double, lon: Double): F[OpenWeatherModel.OneCallRootElement] = {
      client.expect[OpenWeatherModel.OneCallRootElement](GET(path(lat, lon, config)))
        .onError(logError)
        .adaptError { case t => WeatherException(t) }
    }
  }
}
