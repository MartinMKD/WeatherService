package com.jackhenry.weatherservice

import cats.data.NonEmptyList
import cats.effect.Concurrent
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

object OpenWeatherModel {
  final case class OneCallRootElement(current: CurrentElement, alerts: List[AlertsElement])
  object OneCallRootElement {
    implicit val decoder: Decoder[OneCallRootElement] = deriveDecoder[OneCallRootElement]
    implicit val encoder: Encoder[OneCallRootElement] = deriveEncoder[OneCallRootElement]

    implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, OneCallRootElement] = jsonOf
    implicit def entityEncoder[F[_]]: EntityEncoder[F, OneCallRootElement] = jsonEncoderOf
  }

  final case class CurrentElement(temp: Double, weather: NonEmptyList[WeatherElement])
  object CurrentElement {
    implicit val decoder: Decoder[CurrentElement] = deriveDecoder[CurrentElement]
    implicit val encoder: Encoder[CurrentElement] = deriveEncoder[CurrentElement]

    implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, CurrentElement] = jsonOf
    implicit def entityEncoder[F[_]]: EntityEncoder[F, CurrentElement] = jsonEncoderOf
  }

  final case class WeatherElement(description: String)
  object WeatherElement {
    implicit val decoder: Decoder[WeatherElement] = deriveDecoder[WeatherElement]
    implicit val encoder: Encoder[WeatherElement] = deriveEncoder[WeatherElement]

    implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, WeatherElement] = jsonOf
    implicit def entityEncoder[F[_]]: EntityEncoder[F, WeatherElement] = jsonEncoderOf
  }

  final case class AlertsElement(description: String)
  object AlertsElement {
    implicit val decoder: Decoder[AlertsElement] = deriveDecoder[AlertsElement]
    implicit val encoder: Encoder[AlertsElement] = deriveEncoder[AlertsElement]

    implicit def entityDecoder[F[_]: Concurrent]: EntityDecoder[F, AlertsElement] = jsonOf
    implicit def entityEncoder[F[_]]: EntityEncoder[F, AlertsElement] = jsonEncoderOf
  }

  // handle case for no alerts, otherwise we bomb
  implicit val weatherAlertListDecoder: Decoder[List[AlertsElement]] =
    Decoder.decodeOption(Decoder.decodeList[AlertsElement]).map(_.getOrElse(List.empty))

}
