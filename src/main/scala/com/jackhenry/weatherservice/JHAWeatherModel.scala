package com.jackhenry.weatherservice

import enumeratum.{CirceEnum, Enum, EnumEntry}
import cats.effect.IO
import com.jackhenry.weatherservice.OpenWeatherModel.OneCallRootElement
import io.circe._
import io.circe.generic.semiauto._
import org.http4s._
import org.http4s.circe.{jsonEncoderOf, jsonOf}

object JHAWeatherModel {
  sealed trait WeatherFeelsLike extends EnumEntry
  
  object WeatherFeelsLike extends Enum[WeatherFeelsLike] with CirceEnum[WeatherFeelsLike] {
    case object Cold     extends WeatherFeelsLike
    case object Hot      extends WeatherFeelsLike
    case object Moderate extends WeatherFeelsLike

    val values: IndexedSeq[WeatherFeelsLike] = findValues

    def temperatureLabel(temp: Double): WeatherFeelsLike =
      if (temp < 50) Cold
      else if (temp > 80) Hot
      else Moderate
  }

  final case class JHAWeatherModel(
                                       condition: String,
                                       feelsLike: WeatherFeelsLike,
                                       alerts: List[String]
                                     )
  object JHAWeatherModel {
    implicit val encoder: Encoder[JHAWeatherModel] = deriveEncoder[JHAWeatherModel]
    implicit val decoder: Decoder[JHAWeatherModel] = deriveDecoder[JHAWeatherModel]

    implicit val entityDecoder: EntityDecoder[IO, JHAWeatherModel] = jsonOf
    implicit val entityEncoder: EntityEncoder[IO, JHAWeatherModel] = jsonEncoderOf
  }

  implicit class WeatherResOps(weatherRes: OneCallRootElement) {
    def toJHAWeatherModel: JHAWeatherModel = JHAWeatherModel(
      condition = weatherRes.current.weather.head.description,
      feelsLike = WeatherFeelsLike.temperatureLabel(weatherRes.current.temp),
      alerts = weatherRes.alerts.map(_.description)
    )
  }
}
