package com.jackhenry.weatherservice

import cats.effect.{Concurrent, Sync}
import cats.implicits._
import com.jackhenry.weatherservice.WeatherServiceServer.AppConfig
import org.http4s.Method._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

trait WeatherService[F[_]] {
  def getWeather(lat: Double, lon: Double): F[OpenWeatherModel.OneCallRootElement]
}

object WeatherService {
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
  implicit def logger[F[_] : Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  def impl[F[_]: Concurrent](client: Client[F], config: AppConfig): WeatherService[F] = new WeatherService[F]{
    val dsl: Http4sClientDsl[F] = new Http4sClientDsl[F]{}
    import dsl._
    def getWeather(lat: Double, lon: Double): F[OpenWeatherModel.OneCallRootElement] = {
      client.expect[OpenWeatherModel.OneCallRootElement](GET(path(lat, lon, config)))
        .adaptError { case t => WeatherException(t) }
    }
  }
}
