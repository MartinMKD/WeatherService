package com.jackhenry.weatherservice

import cats.effect.Sync
import cats.implicits._
import com.jackhenry.weatherservice.SimpleWeatherModel.WeatherResOps
import io.circe.syntax.EncoderOps
import org.http4s.HttpRoutes
import org.http4s.circe.jsonEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io.QueryParamDecoderMatcher

object WeatherServiceRoutes {
  private object LatQueryParam extends QueryParamDecoderMatcher[Double]("lat")
  private object LonQueryParam extends QueryParamDecoderMatcher[Double]("lon")

  def weatherServiceRoutes[F[_]: Sync](W: WeatherService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "weather" :? LatQueryParam(lat) +& LonQueryParam(lon) =>
        W.getWeather(lat, lon).flatMap {
          openWeatherResult =>
            val result = openWeatherResult.toSimpleWeatherModel
            Ok(result.asJson)
        }
    }
  }
}