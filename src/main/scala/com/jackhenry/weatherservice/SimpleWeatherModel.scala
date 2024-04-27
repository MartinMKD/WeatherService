package com.jackhenry.weatherservice

import enumeratum.{CirceEnum, Enum, EnumEntry}
import cats.effect.IO
import com.jackhenry.weatherservice.OpenWeatherModel.OneCallRootElement
import io.circe._
import io.circe.generic.semiauto._
import org.http4s._
import org.http4s.circe.{jsonEncoderOf, jsonOf}

object SimpleWeatherModel {
  sealed trait WeatherFeelsLike extends EnumEntry
  object WeatherFeelsLike extends Enum[WeatherFeelsLike] with CirceEnum[WeatherFeelsLike] {
    case object Cold     extends WeatherFeelsLike
    case object Hot      extends WeatherFeelsLike
    case object Moderate extends WeatherFeelsLike

    val values: IndexedSeq[WeatherFeelsLike] = findValues

    def fromTemp(temp: Double): WeatherFeelsLike =
      if (temp < 50) Cold
      else if (temp > 80) Hot
      else Moderate
  }

  final case class SimpleWeatherModel(
                                       condition: String,
                                       feelsLike: WeatherFeelsLike,
                                       alerts: List[String]
                                     )
  object SimpleWeatherModel {
    implicit val encoder: Encoder[SimpleWeatherModel] = deriveEncoder[SimpleWeatherModel]
    implicit val entityEncoder: EntityEncoder[IO, SimpleWeatherModel] = jsonEncoderOf
    implicit val decoder: Decoder[SimpleWeatherModel] = deriveDecoder[SimpleWeatherModel]
    implicit val entityDecoder: EntityDecoder[IO, SimpleWeatherModel] = jsonOf
  }

  implicit class WeatherResOps(weatherRes: OneCallRootElement) {
    def toSimpleWeatherModel: SimpleWeatherModel = SimpleWeatherModel(
      condition = weatherRes.current.weather.head.description,
      feelsLike = WeatherFeelsLike.fromTemp(weatherRes.current.temp),
      alerts = weatherRes.alerts.map(_.description)
    )
  }
}
